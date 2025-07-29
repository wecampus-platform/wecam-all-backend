package org.example.wecambackend.dto.responseDTO;

import lombok.*;
import org.example.model.enums.CodeType;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCodeResponse {

    private Long invitationId;

    private String code;
    private String makeUser;

    private CodeType codeType;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private LocalDateTime expiredAt;


}
