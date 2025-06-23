package org.example.wecambackend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultResponse {
    private String userName;
    private String schoolName;
    private String orgName;
    private String enrollYear;
    private int schoolGrade;
}
