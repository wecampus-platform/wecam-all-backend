package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.council.CouncilDepartment;
import org.example.model.council.CouncilDepartmentRole;
import org.example.model.council.CouncilMember;
import org.example.model.enums.MemberRole;
import org.example.model.enums.ExitType;
import org.example.model.enums.UserRole;
import org.example.model.user.User;
import org.example.model.common.BaseEntity;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.requestDTO.DepartmentAssignmentRequest;
import org.example.wecambackend.dto.responseDTO.CouncilCompositionResponse;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.springframework.stereotype.Service;

import org.example.wecambackend.dto.responseDTO.CouncilCompositionResponse;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.dto.responseDTO.DepartmentResponse;
import org.example.wecambackend.repos.CouncilDepartmentRepository;
import org.example.wecambackend.repos.CouncilDepartmentRoleRepository;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.example.wecambackend.repos.UserInformationRepository;
import org.example.wecambackend.repos.UserRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouncilMemberService {

    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilDepartmentRepository councilDepartmentRepository;
    private final CouncilDepartmentRoleRepository councilDepartmentRoleRepository;
    private final UserRepository userRepository;



    /**
     * [설명]
     * - 특정 학생회의 모든 활성화된 구성원(Council Member)을 조회합니다.
     * [필요한 변수]
     * - councilId: 조회할 학생회(organization)의 PK
     * [반환값]
     * - List<CouncilMemberResponse>: 활성화된 학생회 구성원 목록 (DTO 변환된 응답)
     * [호출 위치 / 사용 예시]
     * - 관리자 페이지에서 특정 학생회 구성원 목록을 조회할 때 사용됩니다.
     * - 예: GET /admin/councils/{id}/members 와 같은 컨트롤러에서 호출될 수 있음
     */
    public List<CouncilMemberResponse> getAllCouncilMembers(Long councilId) {
        return councilMemberRepository.findAllActiveMembersByCouncilId(councilId);
    }


    /**
     * 학생회 부원의 부서를 배치합니다.
     * 회장과 부회장만 이 기능을 사용할 수 있습니다.
     */
    public void assignMemberToDepartment(Long memberId, DepartmentAssignmentRequest request) {
        // 1. 대상 부원 조회
        CouncilMember member = councilMemberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND));

        // 2. 현재 학생회에 속한 부원인지 확인
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        if (!member.getCouncil().getId().equals(currentCouncilId)) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        // 3. 부서 조회
        CouncilDepartment department = councilDepartmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND));

        // 4. 부서가 현재 학생회에 속한 부서인지 확인
        if (!department.getCouncil().getId().equals(currentCouncilId)) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        // 5. level 설정
        Integer level = request.getDepartmentLevel() != null ? request.getDepartmentLevel() : 1;

        // 6. 부장(level 0) 배치 시 중복 검사
        if (level == 0) {
            Optional<CouncilMember> existingDirector = councilMemberRepository.findByDepartmentAndRoleLevel(department, 0);
            if (existingDirector.isPresent() && !existingDirector.get().getId().equals(memberId)) {
                throw new BaseException(BaseResponseStatus.DEPARTMENT_ROLE_ALREADY_EXISTS);
            }
        }

        // 7. 부서 배치 업데이트
        member.setDepartment(department);
        
        // 8. 부서 내 역할에 따라 MemberRole 설정
        // 부장(level 0)이면 DIRECTOR, 부원(level 1)이면 DEPUTY로 설정
        if (level == 0) {
            member.setMemberRole(MemberRole.DIRECTOR);
        } else {
            member.setMemberRole(MemberRole.DEPUTY);
        }

        councilMemberRepository.save(member);
    }

    /**
     * 현재 학생회의 모든 부서와 역할 목록을 조회합니다.
     */
    public List<DepartmentResponse> getAllDepartments() {
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        
        // 현재 학생회의 모든 부서 조회
        List<CouncilDepartment> departments = councilDepartmentRepository.findByCouncilId(currentCouncilId);
        
        return departments.stream()
                .map(department -> {
                    // 각 부서의 역할 목록 조회
                    List<CouncilDepartmentRole> roles = councilDepartmentRoleRepository.findByDepartment(department);
                    return DepartmentResponse.from(department, roles);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 학생회 구성원을 제명합니다.
     * 회장과 부회장만 이 기능을 사용할 수 있으며, 회장은 제명할 수 없습니다.
     * 제명된 구성원은 일반 학생으로 복귀하며, 학생회 기능에 접근할 수 없습니다.
     * 
     * @param memberId 제명할 구성원 ID
     * @param reason 제명 사유 (선택사항)
     */
    @Transactional
    public void expelMember(Long memberId, String reason) {
        // 1. 대상 부원 조회
        CouncilMember member = councilMemberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND));

        // 2. 현재 학생회에 속한 부원인지 확인
        Long currentCouncilId = CouncilContextHolder.getCouncilId();
        if (!member.getCouncil().getId().equals(currentCouncilId)) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        // 3. 이미 제명된 부원인지 확인
        if (member.getExitType() != ExitType.ACTIVE) {
            throw new BaseException(BaseResponseStatus.ALREADY_EXPELLED_MEMBER);
        }

        // 4. 회장은 제명할 수 없음
        if (member.getMemberRole() == MemberRole.PRESIDENT) {
            throw new BaseException(BaseResponseStatus.CANNOT_EXPEL_PRESIDENT);
        }

        // 5. 제명 처리 (부서/역할 정보는 보존)
        member.setExitType(ExitType.EXPULSION);
        member.setExpulsionReason(reason);
        member.setExitDate(LocalDateTime.now());

        councilMemberRepository.save(member);

        // 6. 사용자 역할을 STUDENT로 변경 (일반 학생으로 복귀)
        User user = member.getUser();
        user.setRole(UserRole.STUDENT);
        userRepository.save(user);
    }

    public List<CouncilCompositionResponse> getDepartmentCouncilMembers(Long councilId, Long departmentId) {
        return councilMemberRepository.findByCouncilIdAndDepartmentId(councilId,departmentId);
    }
}
