package org.example.wecambackend.dto.request.todo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.enums.ProgressStatus;

@Getter
@Setter
@NoArgsConstructor
public class TodoStatusUpdateRequest {
    @NotNull(message = "진행 상태는 필수입니다.")
    private ProgressStatus progressStatus;
}
