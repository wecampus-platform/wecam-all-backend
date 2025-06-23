package org.example.wecambackend.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailDuplicateCheckResponse {

    @JsonProperty("isDuplicate")
    private boolean isDuplicate;

    @JsonIgnore // Lombok/Jackson 충돌 시 이걸로 차단
    public boolean getDuplicate() {
        return isDuplicate;
    }
}
