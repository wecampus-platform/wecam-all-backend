package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.council.CouncilDepartment;
import org.example.model.council.CouncilDepartmentRole;
import org.example.model.council.CouncilMember;
import org.example.model.common.BaseEntity;
import org.example.model.enums.MemberRole;
import org.example.model.enums.ExitType;
import org.example.model.enums.UserRole;
import org.example.model.user.User;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.projection.CompositionFlatRow;
import org.example.wecambackend.dto.request.DepartmentAssignmentRequest;
import org.example.wecambackend.dto.response.council.CouncilCompositionResponse;
import org.example.wecambackend.dto.response.councilMember.CouncilMemberResponse;
import org.example.wecambackend.dto.response.councilMember.CouncilMemberSearchResponse;
import org.example.wecambackend.dto.response.councilMember.MemberSelectionResponse;
import org.example.wecambackend.repos.council.CouncilDepartmentRepository;
import org.example.wecambackend.repos.council.CouncilDepartmentRoleRepository;
import org.example.wecambackend.repos.council.CouncilMemberRepository;
import org.example.wecambackend.repos.user.UserRepository;
import org.springframework.stereotype.Service;
import org.example.wecambackend.dto.response.department.DepartmentResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CouncilMemberService {

    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilDepartmentRepository councilDepartmentRepository;
    private final CouncilDepartmentRoleRepository councilDepartmentRoleRepository;
    private final UserRepository userRepository;



    /**
     * [설명]
     * - 구성원관리 시 부서 및 학생 정보 추출
     */
    @Transactional(readOnly = true)
    public CompositionResponse getComposition(Long councilId) {
        List<CompositionFlatRow> rows = councilMemberRepository.findCompositionFlat(councilId);

        Map<Long, DepartmentBlock> deptMap = new LinkedHashMap<>();
        List<MemberItem> unassigned = new ArrayList<>();

        for (CompositionFlatRow r : rows) {
            if (r.getDepartmentId() == null) {
                // 미배치
                unassigned.add(new MemberItem(
                        r.getUserId(), r.getUserName(), r.getUserCouncilRole(),
                        r.getDepartmentRoleId(), r.getDepartmentRoleName()
                ));
                continue;
            }

            DepartmentBlock block = deptMap.computeIfAbsent(
                    r.getDepartmentId(),
                    id -> new DepartmentBlock(id, r.getDepartmentName())
            );

            // 부서만 있고 멤버가 없을 수 있음
            if (r.getUserId() != null) {
                boolean isLead = isLeadRole(r.getUserCouncilRole());
                (isLead ? block.lead() : block.sub()).add(new MemberItem(
                        r.getUserId(), r.getUserName(), r.getUserCouncilRole(),
                        r.getDepartmentRoleId(), r.getDepartmentRoleName()
                ));
            }
        }

        return new CompositionResponse(new ArrayList<>(deptMap.values()), unassigned);
    }

    private boolean isLeadRole(String role) {
        if (role == null) return false;
        return switch (role) {
            case "PRESIDENT", "DIRECTOR", "LEADER" -> true;
            default -> false;
        };
    }

    // ===== 응답 DTO (record 예시) =====
    public record CompositionResponse(
            List<DepartmentBlock> departments,
            List<MemberItem> unassigned
    ) {}
    public record DepartmentBlock(
            Long departmentId, String departmentName,
            List<MemberItem> lead, List<MemberItem> sub
    ) {
        public DepartmentBlock(Long id, String name) {
            this(id, name, new ArrayList<>(), new ArrayList<>());
        }
    }
    public record MemberItem(
            Long userId, String userName, String userCouncilRole,
            Long departmentRoleId, String departmentRoleName
    ) {}


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

    /**
     * 학생회 구성원 검색 서비스
     * 이름을 사용하여 현재 학생회의 구성원을 검색합니다.
     * 
     * @param name 검색할 이름
     * @return 검색된 학생회 구성원 목록
     */
    @Transactional(readOnly = true)
    public List<CouncilMemberSearchResponse> searchCouncilMembers(String name) {
        Long councilId = CouncilContextHolder.getCouncilId();
        return councilMemberRepository.searchCouncilMembers(name, councilId);
    }

    public List<CouncilMemberResponse> getAllCouncilMembers(Long councilId) {
        return councilMemberRepository.findAllActiveMembersByCouncilId(councilId);
    }

    /**
     * 회의록/캘린더/할일 할당 시 구성원 선택용 목록 조회
     * 정렬: 본인 → 같은부서 → 나머지 (각 그룹 내 가나다순)
     */
    @Transactional(readOnly = true)
    public List<MemberSelectionResponse> getMemberSelectionList(Long currentUserId) {
        Long councilId = CouncilContextHolder.getCouncilId();

        // 1. 현재 사용자의 CouncilMember 정보 조회
        CouncilMember currentMember = councilMemberRepository
                .findByUserUserPkIdAndCouncilIdAndStatus(currentUserId, councilId, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MEMBER_NOT_FOUND));
        
        // 2. 현재 학생회의 모든 활성 구성원 조회
        List<CouncilMember> allMembers = councilMemberRepository.findAllActiveMembersWithDetailsByCouncilId(councilId);
        
        // 3. 정렬 로직: 본인 → 같은부서 → 나머지 (각 그룹 내 가나다순)
        List<MemberSelectionResponse> sortedMembers = allMembers.stream()
                .map(member -> {
                    // 프로필 썸네일 URL 생성
                    String profileThumbnailUrl = null;
                    if (member.getUser().getUserInformation() != null 
                        && member.getUser().getUserInformation().getProfileImagePath() != null) {
                        String profilePath = member.getUser().getUserInformation().getProfileImagePath();
                        profileThumbnailUrl = "/uploads/" + profilePath.replaceFirst("PROFILE/", "PROFILE_THUMB/");
                    }
                    
                    return MemberSelectionResponse.builder()
                            .councilMemberId(member.getId())
                            .name(member.getUser().getName())
                            .departmentName(member.getDepartment() != null ? member.getDepartment().getName() : null)
                            .profileThumbnailUrl(profileThumbnailUrl)
                            .build();
                })
                .sorted((m1, m2) -> {
                    // 1순위: 본인
                    if (m1.getCouncilMemberId().equals(currentMember.getId())) return -1;
                    if (m2.getCouncilMemberId().equals(currentMember.getId())) return 1;
                    
                    // 2순위: 같은 부서
                    boolean m1SameDept = m1.getDepartmentName() != null && 
                                       m1.getDepartmentName().equals(currentMember.getDepartment() != null ? 
                                           currentMember.getDepartment().getName() : null);
                    boolean m2SameDept = m2.getDepartmentName() != null && 
                                       m2.getDepartmentName().equals(currentMember.getDepartment() != null ? 
                                           currentMember.getDepartment().getName() : null);
                    
                    if (m1SameDept && !m2SameDept) return -1;
                    if (!m1SameDept && m2SameDept) return 1;
                    
                    // 3순위: 가나다순
                    return m1.getName().compareTo(m2.getName());
                })
                .collect(java.util.stream.Collectors.toList());
        
        return sortedMembers;
    }
}
