package org.example.wecambackend.service.client.Affiliation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.projection.OcrEvaluationResult;
import org.example.wecambackend.dto.responseDTO.OcrResultResponse;
import org.example.wecambackend.exception.DuplicateSubmissionException;
import org.example.model.Organization;
import org.example.model.University;
import org.example.model.user.User;
import org.example.model.user.UserSignupInformation;
import org.example.model.affiliation.AffiliationCertification;
import org.example.model.affiliation.AffiliationCertificationId;
import org.example.model.enums.AuthenticationStatus;
import org.example.model.enums.AuthenticationType;
import org.example.model.enums.OcrResult;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.repos.affiliation.AffiliationCertificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class AffiliationService {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final AffiliationFileService affiliationFileService;
    private final OcrService ocrService;
    private final AffiliationCertificationRepository affiliationCertificationRepository;
    private final UserSignupInformationRepository userSignupInformationRepository;
    private final SchoolRepository schoolRepository;
    private final OrganizationRepository organizationRepository;

    /*TODO : 좋지 않은 접속을 막기 위해 서비스 딴에 해뒀지만, 상태저장으로 UI/UX 버튼 비활성화도 필요.
       신입생 인증 진행 시 role 변환을 할 거니까 적용됨. 다만, ROle UPDATE 시점은 승인 후 이기 떄문에, 그전까지는 버튼이 비활성화 되지 않음.
       이거또한 UI에 구현할 것인지 얘기 해봐야한다.*/

    // Student= New 랑 Current 합침!
    //DB 한꺼번에 저장. 업로드되자마자 OCR 결과추출해서 Affiliation 테이블에 값이 들어가는 거 까지가 하나의 로직
    @Operation(summary = "인증서 등록 서비스", description = "사진 저장, OCR 추출 결과를 DB에 저장 전체 Transactional로 묶임.")
    @Transactional
    public void saveStudentAffiliation(Long userId, MultipartFile file, AuthenticationType status) {

        //1. 유저조회
        User uploadUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 2. 이미 인증 요청이 존재하는지 확인
        boolean exists = affiliationCertificationRepository.existsByUserAndAuthenticationType(uploadUser, status);
        if (exists) {
            throw new DuplicateSubmissionException("이미 해당 유형의 인증 요청을 제출하셨습니다.");
        }

        // 3. 회원가입 정보 조회
        UserSignupInformation signupInfo = userSignupInformationRepository.findByUser_UserPkId(userId)
                .orElseThrow(() -> new RuntimeException("회원가입 정보 없음"));

        // 4. 학교/소속 정보 조회
        University school = schoolRepository.findById(signupInfo.getSelectSchoolId())
                .orElseThrow(() -> new RuntimeException("학교 정보 없음"));

        Organization organization = organizationRepository.findById(signupInfo.getSelectOrganizationId())
                .orElseThrow(() -> new RuntimeException("소속 정보 없음"));

        // 5. OCR 수행 → 결과 DTO로 매핑
        Map<String, String> result = ocrService.requestOcr(file);
        String raw = result.get("schoolGrade");
        int schoolGrade;
        //OCR 결과값이 제대로 반출 안됐을 것을 고려해서, 자동으로 신입생일 경우 1 고정 , 아닐 경우 ocr결과를 기반으로 하되, 에러처리
        //TODO : OCR 만들 때, 제대로 1,2,3,4 로 나오게끔 하고 만약 값처리가 분명하지 않다면 0 으로 처리하게끔 해줘야될듯.
        if (status == AuthenticationType.NEW_STUDENT) {
            schoolGrade = 1;
        } else {
            try {
                schoolGrade = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("OCR 결과 schoolGrade가 숫자가 아닙니다: " + raw);
            }
        }
        OcrResultResponse ocrResultDto = OcrResultResponse.builder()
                .userName(result.get("userName"))
                .schoolName(result.get("schoolName"))
                .orgName(result.get("orgName"))
                .enrollYear(result.get("enrollYear"))
                .schoolGrade(schoolGrade)
                .build();

        // 6. OCR 결과 판단
        OcrEvaluationResult ocrResult = determineFreshmanOcrResult(signupInfo, ocrResultDto, school, organization);

        AffiliationCertification cert;
        // 7. 인증 정보 저장 - 신입생 인증 정보 저장

        AffiliationCertificationId id = new AffiliationCertificationId(
                uploadUser.getUserPkId(),
                status);


         cert = AffiliationCertification.builder()
                .id(id)
                .user(uploadUser)
                .authenticationType(status)
                .ocrUserName(ocrResultDto.getUserName())
                .ocrEnrollYear(ocrResultDto.getEnrollYear())
                .ocrSchoolName(ocrResultDto.getSchoolName())
                .ocrOrganizationName(ocrResultDto.getOrgName())
                 .ocrschoolGrade(ocrResultDto.getSchoolGrade())
                .ocrResult(ocrResult.getResult())
                 .reason(ocrResult.getReason())
                .status(AuthenticationStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .organization(organization)
                .university(school)
                 .username(signupInfo.getName())
                .build();

        affiliationCertificationRepository.save(cert);
        // 8. 파일 정보 저장
        UUID uuid = UUID.randomUUID();
        String path = fileStorageService.save(file,uuid);
        affiliationFileService.saveToDB(cert, file, path,uuid);

    }

    //신입생 OCR 결과값 평가
    public OcrEvaluationResult determineFreshmanOcrResult(
            UserSignupInformation signupInfo,
            OcrResultResponse ocrResult,
            University school,
            Organization org) {

        int matchCount = 0;
        List<String> matchedFields = new ArrayList<>();

        // 1. 이름 비교
        if (equalsIgnoreCaseSafe(signupInfo.getName(), ocrResult.getUserName())) {
            matchCount++;
            matchedFields.add("이름");
        }

        // 2. 입학년도 비교
        if (signupInfo.getEnrollYear().equals(ocrResult.getEnrollYear())) {
            matchCount++;
            matchedFields.add("입학년도");
        }

        // 3. 학교 이름 비교 (부분 일치 허용)
        if (school.getSchoolName().contains(ocrResult.getSchoolName())) {
            matchCount++;
            matchedFields.add("학교명");
        }

        // 4. 소속 이름 비교 (부분 일치 허용)
        if (org.getOrganizationName().contains(ocrResult.getOrgName())) {
            matchCount++;
            matchedFields.add("소속명");
        }

        // OCR 실패 판단 (빈 값 포함)
        if (Stream.of(
                ocrResult.getUserName(),
                ocrResult.getSchoolName(),
                ocrResult.getOrgName(),
                ocrResult.getEnrollYear()
        ).anyMatch(s -> s == null || s.isBlank())) {
            return new OcrEvaluationResult(OcrResult.FAILURE, "OCR 결과에 누락된 정보가 존재합니다.");
        }

        // 판단 기준
        if (matchCount == 4) {
            return new OcrEvaluationResult(OcrResult.SUCCESS, "모든 정보가 일치합니다: " + String.join(", ", matchedFields));
        } else if (matchCount >= 1) {
            return new OcrEvaluationResult(OcrResult.UNCLEAR, "일치한 정보: " + String.join(", ", matchedFields));
        } else {
            return new OcrEvaluationResult(OcrResult.FAILURE, "일치하는 정보가 없습니다.");
        }
    }

    private boolean equalsIgnoreCaseSafe(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }
}
