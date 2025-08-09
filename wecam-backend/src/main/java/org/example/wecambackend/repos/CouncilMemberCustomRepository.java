package org.example.wecambackend.repos;

import org.example.wecambackend.dto.responseDTO.CouncilMemberSearchResponse;

import java.util.List;

public interface CouncilMemberCustomRepository {
    
    /**
     * 학생회 구성원 검색
     * 
     * @param name 검색할 이름
     * @param councilId 현재 학생회 ID
     * @return 학생회 구성원 검색 결과 목록
     */
    List<CouncilMemberSearchResponse> searchCouncilMembers(String name, Long councilId);
}
