package org.example.wecambackend.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 409 : 중복됨
     */
    EMAIL_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
    PHONE_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 전화번호입니다."),
    EMAIL_PHONE_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이메일과 전화번호가 모두 사용 중입니다."),

    /**
     * 500 : Database, Server 오류
     */
    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다.")
    ;


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
