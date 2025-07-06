package org.example.wecambackend.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 꼭 필요
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED; // 401
    }
}
