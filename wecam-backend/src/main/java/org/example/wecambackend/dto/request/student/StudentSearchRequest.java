package org.example.wecambackend.dto.request.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Schema(description = "일반 학생 검색 요청 DTO")
public class StudentSearchRequest {
    
    @Schema(description = "검색할 이름", example = "김철수", required = true)
    @NotBlank(message = "검색할 이름을 입력해주세요.")
    private String name;
    
    @Schema(description = "입학년도 필터 (19~25, 다중 선택 가능, 생략 시 전체)", example = "[\"25\", \"24\"]")
    private List<String> year;
    
    @Schema(description = "학년 필터 (1~4, 다중 선택 가능, 생략 시 전체)", example = "[1, 2]")
    private List<Integer> grade;
}
