package shoppingmall.ankim.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER_NOT_FOUND"),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}