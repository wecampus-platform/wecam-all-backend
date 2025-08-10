package org.example.wecambackend.repos.user;

import org.example.wecambackend.dto.response.StudentSearchResponse;

import java.util.List;

public interface UserCustomRepository {
    
    /**
     * 일반 학생 검색
     * 
     * @param name 검색할 이름
     * @param years 입학년도 필터 (null이면 전체)
     * @param grades 학년 필터 (null이면 전체)
     * @return 일반 학생 검색 결과 목록
     */
    List<StudentSearchResponse> searchStudents(String name, List<String> years, List<Integer> grades);
}
