package org.example.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "진행 상태",
        allowableValues = {"NOT_STARTED", "IN_PROGRESS", "COMPLETED"})
public enum ProgressStatus {
    NOT_STARTED, // 진행 전
    IN_PROGRESS, // 진행 중
    COMPLETED // 진행 완료
}
