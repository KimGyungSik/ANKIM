package shoppingmall.ankim.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER_NOT_FOUND"),

    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."), // 메일 전송에 실패했습니다.
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    MISSING_REQUIRED_ID(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}