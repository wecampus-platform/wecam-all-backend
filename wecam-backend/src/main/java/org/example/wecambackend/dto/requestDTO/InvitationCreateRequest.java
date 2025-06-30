package org.example.wecambackend.dto.requestDTO;

import lombok.*;
import org.example.model.enums.CodeType;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCreateRequest {


    private CodeType codeType;
    private Boolean isUsageLimit;
    private Integer usageLimit;

}
