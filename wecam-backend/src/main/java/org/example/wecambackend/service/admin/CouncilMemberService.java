package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.Council;
import org.example.wecambackend.dto.responseDTO.CouncilMemberResponse;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouncilMemberService {

    public List<CouncilMemberResponse> getAllCouncilMembers(Long councilId) {
        return councilMemberRepository.findAllActiveMembersByCouncilId(councilId);

    }

    private final CouncilMemberRepository councilMemberRepository;
}
