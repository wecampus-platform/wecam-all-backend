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



    PARTIAL_SUCCESS(true, 207, "일부 항목 처리에 실패했습니다."),

    /**
     * 400 : Request, Response 오류
     */
    INVALID_FIELD_VALUE(false, HttpStatus.BAD_REQUEST.value(), "필드 값이 올바르지 않습니다."),
    INVALID_FILE_INPUT(false, HttpStatus.BAD_REQUEST.value(), "빈 파일은 저장할 수 없습니다."),
    MISSING_COUNCIL_ID_HEADER(false, HttpStatus.BAD_REQUEST.value(), "X-Council-Id 헤더가 없습니다."),
    PATH_VARIABLE_NOT_FOUND(false, HttpStatus.BAD_REQUEST.value(), "요청 경로 변수 정보를 찾을 수 없습니다."),
    EMPTY_FILENAME(false, HttpStatus.BAD_REQUEST.value(), "파일명이 비어 있습니다."),
    INVALID_INPUT(false, HttpStatus.BAD_REQUEST.value(), "입력값이 유효하지 않습니다."),
    INVALID_INVITE_CODE(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 초대코드입니다."),
    MISSING_SCHOOL_INFO(false, HttpStatus.BAD_REQUEST.value(), "학교 정보가 누락되었습니다."),
    INVALID_ORG_TYPE(false, HttpStatus.BAD_REQUEST.value(), "알맞지 않은 조직 유형입니다."),
    INVALID_COLLEGE_ORG(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 단과대학 조직입니다."),
    INVALID_DEPARTMENT_ORG(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 학과 조직입니다."),
    INVALID_USER(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 사용자입니다."),
    INVALID_SIGNUP_REQUEST(false, HttpStatus.BAD_REQUEST.value(),"회원가입 필수값이 누락되었습니다."),
    //초대코드 만료
    INVITATION_CODE_EXPIRED(false, HttpStatus.BAD_REQUEST.value(), "만료된 초대코드입니다."),

    //초대코드 기간 설정 잘못함
    INVALID_EXPIRATION_TIME(false, HttpStatus.BAD_REQUEST.value(), "만료일은 현재 시각보다 최소 5분 이후여야 합니다."),

    //초대코드 존재하지 않음
    INVITATION_CODE_NOT_FOUND(false, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 초대코드입니다."),

    MISSING_COUNCIL_ID_PARAM(false, HttpStatus.BAD_REQUEST.value(), "councilId 파라미터를 찾을 수 없습니다."),
    MISSING_ENTITY_ID_PARAM(false,  HttpStatus.BAD_REQUEST.value(), "리소스 ID 파라미터가 누락되었습니다."),

    FILE_EMPTY(false, HttpStatus.BAD_REQUEST.value(), "빈 파일은 저장할 수 없습니다."),

    /**
     * 401 : 인증 필요
     */
    UNAUTHORIZED(false, HttpStatus.UNAUTHORIZED.value(), "로그인이 필요한 요청입니다."),
    NOT_AUTHENTICATED_USER(false, HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다."),

    //비밀번호 일치하지 않을 때
    PASSWORD_NOT_MATCHED(false, HttpStatus.UNAUTHORIZED.value(), "패스워드가 일치하지 않습니다."),


    /**
     *403 : 권한 없음
     */

    // 404: Not Found
    REQUEST_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 요청을 찾을 수 없습니다."),
    FILE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "삭제하려는 파일이 존재하지 않습니다."),
    ENTITY_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 대상을 찾을 수 없습니다."),
    COUNCIL_NOT_FOUND(false,  HttpStatus.NOT_FOUND.value(), "학생회 정보가 없습니다."),
    ORGANIZATION_NOT_FOUND(false,  HttpStatus.NOT_FOUND.value(), "해당 조직을 찾을 수 없습니다."),
    SCHOOL_NOT_FOUND(false,  HttpStatus.NOT_FOUND.value(), "해당 학교를 찾을 수 없습니다."),
    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 유저를 찾을 수 없습니다."),
    INVITE_CODE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 초대코드가 존재하지 않습니다."),
    PHONE_INFO_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "전화번호 정보가 없습니다."),
    //로그인 시 이메일 정보 일치하지 않을 떄
    EMAIL_INFO_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "이메일 정보가 없습니다."),
    ACCESS_DENIED_REQUEST(false, HttpStatus.NOT_FOUND.value(), "이미 승인됐거나 거절된 요청입니다."),



    // 403: Forbidden : 권한 없을 때 쓰는 거
    NO_PERMISSION_TO_MANAGE(false, HttpStatus.FORBIDDEN.value(), "해당 요청을 관리할 권한이 없습니다."),
    COUNCIL_MISMATCH(false, HttpStatus.FORBIDDEN.value(), "학생회가 불일치 합니다."),
    ACCESS_DENIED(false, HttpStatus.FORBIDDEN.value(), "접근이 불가합니다."),
    ROLE_REQUIRED(false, HttpStatus.FORBIDDEN.value(), "접근을 위해 필요한 권한이 부족합니다."),
    ONLY_AUTHOR_CAN_MODIFY(false, HttpStatus.FORBIDDEN.value(), "작성자만 수정할 수 있습니다."),
    INVALID_COUNCIL_ACCESS(false, HttpStatus.FORBIDDEN.value(), "요청한 조직에 대한 접근 권한이 없습니다."),


    /**
     * 409 : 중복됨
     */
    EMAIL_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
    PHONE_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 전화번호입니다."),
    EMAIL_PHONE_DUPLICATED(false, HttpStatus.CONFLICT.value(), "이메일과 전화번호가 모두 사용 중입니다."),
    ALREADY_PROCESSED(false, HttpStatus.CONFLICT.value(), "이미 처리된 인증 요청입니다."),
    AFFILIATION_ALREADY_EXISTS(false, HttpStatus.CONFLICT.value(), "이미 해당 유형의 인증 요청을 제출하셨습니다."),
    COUNCIL_ALREADY_EXISTS(false, 409, "이미 학생회가 존재하는 조직입니다."),

    /**
     * 500 : Database, Server,file save 오류
     */
    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다."),
    FILE_SAVE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 저장에 실패했습니다."),
    FILE_DELETE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 저장에 실패했습니다."),
    COUNCIL_ID_EXTRACTION_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "councilId 추출에 실패했습니다."),
    USER_TAG_GENERATION_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용 가능한 userTag를 찾을 수 없습니다.");






    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
