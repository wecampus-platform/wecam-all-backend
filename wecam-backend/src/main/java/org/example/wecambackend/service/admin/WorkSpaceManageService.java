package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.model.council.Council;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkSpaceManageService {
// 워크스페이스 뷰 - 워크스페이스에 있는 targetOrg 의 parentId 가 Council 의 OrganizationId 와 동일하다면
// 해당 학생회에게 워크스페이스 승인 인가를 줌.


//    //TODO : 나중에 org 값도 자주 쓰일 거 같으니까,,,, X-councilId 넣을 때 조직값도 같이 넣게끔 해야될듯..?
//    //워크스페이스 승인 요청 리스트 반환
//    @Transactional(readOnly = true)
//    public list<> getAllWorkspaceRequest(Long councilId ) {
//        Council council = EntityFinderService.getCouncilByIdOrThrow(councilId);
//        return
//    }
}
