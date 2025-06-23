package org.example.wecambackend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.model.University;

@Getter
@AllArgsConstructor
public class UniversitySimpleResponse {
    private Long id;
    private String name;

    //엔티티 DTO 변환
    public static UniversitySimpleResponse fromEntity(University uni) {
        return new UniversitySimpleResponse(uni.getSchoolId(),uni.getSchoolName());
    }
}
