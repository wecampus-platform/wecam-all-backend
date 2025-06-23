package org.example.wecamadminbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.OrganizationRequest;
import org.example.model.enums.RequestStatus;
import org.example.wecamadminbackend.dto.request.OrganizationRequestDTO;
import org.example.wecamadminbackend.repos.OrganizationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrganizationService {

    private final OrganizationRequestRepository organizationRequestRepository;


    @Transactional
    public void approveWorkspaceRequest(Long requestId) {
        OrganizationRequest request = organizationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        // 승인 처리
        request.setStatus(RequestStatus.APPROVED);
        createWorkspace(request.getCouncilName());
        organizationRequestRepository.save(request);
    }

    public List<OrganizationRequestDTO> getPendingRequests() {
        List<OrganizationRequestDTO> organizationRequestDTOS =  organizationRequestRepository.findRequestDtosByStatus(RequestStatus.PENDING);
        return organizationRequestDTOS;
    }

    private void createWorkspace(String councilName) {
    }
}
