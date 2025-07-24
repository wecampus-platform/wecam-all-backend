package org.example.wecambackend.common.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.example.wecambackend.common.response.BaseResponseStatus.INVALID_FIELD_VALUE;

@Hidden
@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    // 알 수 없는 에러에 대한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> ExceptionHandle(Exception exception) {
        log.error("Exception has occured. ", exception);
        BaseResponse<?> body = new BaseResponse<>(
                BaseResponseStatus.UNEXPECTED_ERROR,
                exception.getMessage()
            );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 상태 코드 설정
                .body(body);
    }

    // BaseException에 대한 예외 처리
    @ExceptionHandler(BaseException.class)
    public BaseResponse<BaseResponseStatus> BaseExceptionHandle(BaseException exception) {
        log.warn("BaseException. error message: {}", exception.getMessage());

        return new BaseResponse(exception.getStatus(), exception.getData());
    }

    // 유효성 검사(@Valid)에 대한 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException has occured.", e);
        return new BaseResponse<>(INVALID_FIELD_VALUE, e.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    protected BaseResponse<?> handleBindException(BindException e) {
        log.error("BindException has occured.", e);
        return new BaseResponse<>(INVALID_FIELD_VALUE, e.getBindingResult());
    }
}
