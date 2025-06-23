package org.example.wecambackend.common.exceptions;


import lombok.Getter;
import lombok.Setter;
import org.example.wecambackend.common.response.BaseResponseStatus;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private BaseResponseStatus status;
    private Object data;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BaseException(BaseResponseStatus status, Object data) {
        super(status.getMessage());
        this.status = status;
        this.data = data;
    }
}
