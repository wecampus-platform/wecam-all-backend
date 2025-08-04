package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.council.CouncilMember;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouncilCompositionService {

    public List<CouncilMemberResponse> getUnassignedCouncilMembers(Long councilId) {
        return councilMemberRepository.findAllActiveMembersByCouncilId(councilId);
    }

    public List<CouncilMemberResponse> getDepartmentCouncilMembers(Long councilId,Long departmentId) {
        return councilMemberRepository.findByCouncilIdAndDepartmentOrUnassigned(councilId,departmentId);
    }

    private final CouncilMemberRepository councilMemberRepository;
}
