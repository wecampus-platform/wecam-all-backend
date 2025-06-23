package org.example.wecambackend.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//TODO : 왠지모르겠지만 이거 주석 처리 하니까 잘 뜸...Swagger 추후 다시 주석 풀어야됨.
//위에 문제 해결, HIDDEN 다니까 괜찮아짐.
@Hidden
@RestControllerAdvice
@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // 1. 접근 권한 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error =  new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "접근 권한이 없습니다.",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 2. 일반 예외 처리 (디버깅용)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다.",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // 3. 복합키 중복 예외 전역 처리 - 중복된 리소스 생성 , 처리된 서류 한번 더 요청 시
    @ExceptionHandler(DuplicateSubmissionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSubmission(DuplicateSubmissionException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),  // 409
                ex.getMessage(),
                "중복 제출"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 4. 로그인 필요 메세지
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), // 401
                ex.getMessage(),
                "로그인 필요"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * BaseException 예외 처리 핸들러
     *
     * 서비스 계층 등에서 throw new BaseException(...)으로 발생시킨 예외를 처리합니다.
     */
    @ExceptionHandler(BaseException.class)
    public BaseResponse<BaseResponseStatus> BaseExceptionHandle(BaseException exception) {
        log.warn("BaseException. error message: {}", exception.getMessage());

        return new BaseResponse(exception.getStatus(), exception.getData());
    }
}
