package org.example.wecambackend.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "초대코드 사용 이력 응답 DTO")
public class InvitationUsedHistoryResponse implements Serializable {

    @Schema(description = "초대코드 PK", example = "123")
    private Long invitationPkId;

    @Schema(description = "초대코드 사용 시각", example = "2025-07-20T14:22:35")
    private LocalDateTime usedAtTime;

    @Schema(description = "초대코드를 사용한 유저 정보 , DTO 형식")
    private CouncilVisibleUserDTO userInfoDTO;
}
