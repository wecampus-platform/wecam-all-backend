package org.example.wecambackend.dto.response.invitation;

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

    @Schema(description = "초대코드를 사용한 유저 정보 , 추후 사용이력에서 유저 아이디 클릭 시 유저 DTO 반환을 위해 필요함. ")
    private Long userPkId;

    @Schema(description = "초대코드를 사용한 유저 이름. VIEW 를 위해 필요함 ")
    private String userName;

    //TODO : 추후 마스킹 처리 (권한 분기)
    @Schema(description = "초대코드를 사용한 유저 이름. VIEW 를 위해 필요함. 우선 아이디 대신으로 이메일 보이게 함. 마스킹 ")
    private String userEmail;
}
