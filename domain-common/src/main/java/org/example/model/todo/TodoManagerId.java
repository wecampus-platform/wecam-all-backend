package org.example.model.todo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TodoManagerId implements Serializable {

    // 할일 ID
    private Long todoId;

    // 담당자(유저) ID
    private Long userPkId;

}
