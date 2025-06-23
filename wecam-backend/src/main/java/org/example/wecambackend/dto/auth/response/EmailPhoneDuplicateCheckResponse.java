package org.example.wecambackend.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailPhoneDuplicateCheckResponse {

    @JsonProperty("isEmailDuplicate")
    private boolean isEmailDuplicate;

    @JsonProperty("isPhoneNumberDuplicate")
    private boolean isPhoneNumberDuplicate;

    @JsonIgnore // Lombok/Jackson 충돌 시 이걸로 차단
    public boolean getEmailDuplicate() {
        return isEmailDuplicate;
    }

    @JsonIgnore
    public boolean getPhoneNumberDuplicate() {
        return isPhoneNumberDuplicate;
    }
}