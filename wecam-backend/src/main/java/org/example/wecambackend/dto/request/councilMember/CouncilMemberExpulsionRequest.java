package org.example.wecambackend.dto.request.councilMember;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouncilMemberExpulsionRequest {
    
    @Size(max = 50, message = "제명 사유는 50자 이하여야 합니다.")
    private String reason;
} 