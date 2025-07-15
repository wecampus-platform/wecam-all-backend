package org.example.wecambackend.service.client.Affiliation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.projection.OcrEvaluationResult;
import org.example.wecambackend.dto.responseDTO.OcrResultResponse;
import org.example.model.organization.Organization;
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
import org.example.wecambackend.repos.organization.OrganizationRepository;
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
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND));

        // 2. 이미 인증 요청이 존재하는지 확인
        boolean exists = affiliationCertificationRepository.existsByUserAndAuthenticationType(uploadUser, status);
        if (exists) {
            throw new BaseException(BaseResponseStatus.AFFILIATION_ALREADY_EXISTS);
        }

        // 3. 회원가입 정보 조회
        UserSignupInformation signupInfo = userSignupInformationRepository.findByUser_UserPkId(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));

        // 4. 학교/소속 정보 조회
        University school = schoolRepository.findById(signupInfo.getSelectSchoolId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.SCHOOL_NOT_FOUND));

        Organization organization = organizationRepository.findById(signupInfo.getSelectOrganizationId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));

        // 5. OCR 수행 → 결과 DTO로 매핑
        Map<String, Object> result = ocrService.requestOcr(file);

        // 내부 text 맵 안전하게 꺼내기
        Object textObj = result.get("text");
        if (!(textObj instanceof Map)) {
            throw new BaseException(BaseResponseStatus.INVALID_FIELD_VALUE);
        }

        Map<?, ?> textMap = (Map<?, ?>) textObj;

        Object rawGrade = textMap.get("schoolGrade");
        if (rawGrade == null) {
            throw new BaseException(BaseResponseStatus.INVALID_FIELD_VALUE, "schoolGrade가 null입니다");
        }

        int schoolGrade;
        if (status == AuthenticationType.NEW_STUDENT) {
            schoolGrade = 1;
        } else {
            try {
                schoolGrade = Integer.parseInt(rawGrade.toString());
            } catch (NumberFormatException e) {
                throw new BaseException(BaseResponseStatus.INVALID_FIELD_VALUE);
            }
        }

        OcrResultResponse ocrResultDto = OcrResultResponse.builder()
                .userName((String) textMap.get("userName"))
                .schoolName((String) textMap.get("schoolName"))
                .orgName((String) textMap.get("orgName"))
                .enrollYear((String) textMap.get("enrollYear"))
                .schoolGrade(schoolGrade)
                .issuanceDate(LocalDateTime.parse((String) textMap.get("issuanceDate")))
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
                 .issuanceDate(ocrResultDto.getIssuanceDate())
                 .reason(ocrResult.getReason())
                .status(AuthenticationStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .organization(organization)
                .university(school)
                 .username(signupInfo.getName())
                 .selOrganizationName(organization.getOrganizationName())
                 .selEnrollYear(signupInfo.getEnrollYear())
                 .selSchoolName(school.getSchoolName())
                .build();

        affiliationCertificationRepository.save(cert);
        // 8. 파일 정보 저장
        UUID uuid = UUID.randomUUID();
        Map<String, String> fileInfo = fileStorageService.save(file, uuid);
        String filePath = fileInfo.get("filePath");
        String fileUrl = fileInfo.get("fileUrl");

        affiliationFileService.saveToDB(cert, file, filePath, fileUrl, uuid);


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
