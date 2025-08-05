package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.council.Council;
import org.example.model.council.CouncilDepartment;
import org.example.model.council.CouncilDepartmentRole;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.repos.CouncilDepartmentRepository;
import org.example.wecambackend.repos.CouncilDepartmentRoleRepository;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouncilDepartmentService {



    //학생회 안의 부서 생성 (기본 값("새로운부서")로 생성하기) , 부서 생성 후 role 도 기본으로 생성해두기 (부장,부원으로)
    @Transactional
    public void createCouncilDepartment(Long councilId) {
        Council currentCouncil = entityFinderService.getCouncilByIdOrThrow(councilId);
        CouncilDepartment newCouncilDP = CouncilDepartment.builder()
                .council(currentCouncil)
                .build();
        councilDepartmentRepository.save(newCouncilDP);
        CouncilDepartmentRole newCouncilDepartRole = CouncilDepartmentRole.builder()
                .department(newCouncilDP)
                .name("부장")
                .level(0)
                .build();
        CouncilDepartmentRole newCouncilDepartRole2 = CouncilDepartmentRole.builder()
                .department(newCouncilDP)
                .name("부원")
                .level(1)
                .build();
        councilDepartmentRoleRepository.save(newCouncilDepartRole);
        councilDepartmentRoleRepository.save(newCouncilDepartRole2);

    }

    //부서 이름 변경
    @Transactional
    public void modifyCouncilDepartmentName(Long councilId,Long councilDepartmentId,String reName){
        CouncilDepartment councilDp = councilDepartmentRepository.findById(councilDepartmentId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ORGANIZATION_NOT_FOUND));
        councilDp.setName(reName);
        councilDepartmentRepository.save(councilDp);
    }

    private final EntityFinderService entityFinderService;
    private final CouncilDepartmentRepository councilDepartmentRepository;
    private final CouncilDepartmentRoleRepository councilDepartmentRoleRepository;
}
