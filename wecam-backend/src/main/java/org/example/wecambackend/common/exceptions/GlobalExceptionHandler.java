package org.example.wecambackend.common.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import org.example.wecambackend.common.response.BaseResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {

        return ResponseEntity
                .status(HttpStatusCode.valueOf(e.getStatus().getCode()))
                .body(new BaseResponse<>(e.getStatus(), (Object) null));
    }
}
