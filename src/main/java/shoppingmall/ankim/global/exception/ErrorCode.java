package shoppingmall.ankim.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER_NOT_FOUND");

    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}