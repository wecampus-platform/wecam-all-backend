package org.example.wecambackend.dto.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentExpulsionRequest {
    
    @NotBlank(message = "제명 사유는 필수입니다.")
    @Size(max = 50, message = "제명 사유는 50자 이하여야 합니다.")
    private String reason;
} 