package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.wecambackend.dto.responseDTO.CouncilCompositionResponse;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouncilMemberService {

    private final CouncilMemberRepository councilMemberRepository;



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

    public List<CouncilCompositionResponse> getDepartmentCouncilMembers(Long councilId, Long departmentId) {
        return councilMemberRepository.findByCouncilIdAndDepartmentId(councilId,departmentId);
    }
}
