package org.example.wecambackend.dto.Enum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "할 일 타입",
        allowableValues = {"MY_TODO", "RECEIVED_TODO", "SENT_TODO", "ALL_TODO"})
public enum TodoTypeDTO {
    MY_TODO,        // 내가 작성했고, 나도 담당자인 할 일
    RECEIVED_TODO,  // 다른 사람이 작성했고, 내가 담당자인 할 일
    SENT_TODO,       // 내가 작성했고, 다른 사람이 담당자인 할 일
    ALL_TODO // 모두
}
