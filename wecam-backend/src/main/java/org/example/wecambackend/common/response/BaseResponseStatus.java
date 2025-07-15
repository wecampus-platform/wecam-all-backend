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
     * 400 : Request, Response 오류
     */
    INVALID_FIELD_VALUE(false, HttpStatus.BAD_REQUEST.value(), "필드 값이 올바르지 않습니다."),
    INVALID_FILE_INPUT(false, HttpStatus.BAD_REQUEST.value(), "빈 파일은 저장할 수 없습니다."),
    // 404: Not Found
    REQUEST_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 요청을 찾을 수 없습니다."),
    FILE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "삭제하려는 파일이 존재하지 않습니다."),

    // 403: Forbidden
    NO_PERMISSION_TO_MANAGE(false, HttpStatus.FORBIDDEN.value(), "해당 요청을 관리할 권한이 없습니다."),


    /**
     * 409 : 중복됨
     */
    EMAIL_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
    PHONE_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 전화번호입니다."),
    EMAIL_PHONE_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이메일과 전화번호가 모두 사용 중입니다."),

    /**
     * 500 : Database, Server,file save 오류
     */
    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다."),
    FILE_SAVE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 저장에 실패했습니다."),
    FILE_DELETE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 저장에 실패했습니다."),
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
