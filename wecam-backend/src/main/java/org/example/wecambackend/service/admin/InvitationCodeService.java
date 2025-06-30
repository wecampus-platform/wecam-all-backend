package org.example.wecambackend.service.admin;


import lombok.RequiredArgsConstructor;
import org.example.model.Council;
import org.example.model.Organization;
import org.example.model.invitation.InvitationCode;
import org.example.model.invitation.InvitationHistory;
import org.example.model.user.User;
import org.example.wecambackend.dto.requestDTO.InvitationCreateRequest;
import org.example.wecambackend.dto.responseDTO.InvitationCodeResponse;
import org.example.wecambackend.repos.CouncilRepository;
import org.example.wecambackend.repos.InvitationCodeRepository;
import org.example.wecambackend.repos.InvitationHistoryRepository;
import org.example.wecambackend.repos.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationCodeService {
    private final InvitationCodeRepository invitationCodeRepository;
    private final InvitationHistoryRepository invitationHistoryRepository;
    private final UserRepository userRepository;
    private final CouncilRepository councilRepository;

    public List<InvitationCodeResponse> findByCouncilId(Long councilId) {
        return invitationCodeRepository.findAllByCouncilId(councilId);
    }

    @Transactional
    public void createInvitationCodeByStudent(InvitationCreateRequest requestDto, Long userId,Long councilId){
    // 1. 유저 조회
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

    // 2. 유저가 소속된 조직 또는 학생회 가져오기 (예: 학과 학생회 기준)
    Council council = councilRepository.findById(councilId)
            .orElseThrow(() -> new IllegalArgumentException("COUNCIL_NOT_FOUND"));

    // 3. 소속 조직
    Organization organization = council.getOrganization();

    String generatedCode = generateUniqueCode();  // 여기서 자동 생성


        // 4. 초대코드 엔티티 생성
    InvitationCode invitationCode = InvitationCode.builder()
            .code(generatedCode)
            .codeType(requestDto.getCodeType())
            .isUsageLimit(requestDto.getIsUsageLimit())
            .usageLimit(requestDto.getIsUsageLimit() ? requestDto.getUsageLimit() : null)
            .isActive(true)
            .user(user)
            .council(council)
            .organization(organization)
            .build();

    // 5. 저장
    invitationCodeRepository.save(invitationCode);
    }

    private static final int CODE_LENGTH = 10;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode(CODE_LENGTH);
        } while (invitationCodeRepository.existsByCode(code));
        return code;
    }

    private String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
