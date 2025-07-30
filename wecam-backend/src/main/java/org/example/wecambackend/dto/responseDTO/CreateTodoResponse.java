package org.example.wecambackend.dto.responseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreateTodoResponse {

    private String code;
    private LocalDateTime expiredAt;
}
