package shoppingmall.ankim.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 약관 관련 에러 코드
    REQUIRED_TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "필수 약관에 동의하지 않았습니다."),

    // 이메일 인증 관련 에러 코드
    LOGINID_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."), // 메일 전송에 실패했습니다.
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    MISSING_REQUIRED_ID(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),

    // 회원정보 관련 에러 코드
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_STATUS_LOCKED(HttpStatus.FORBIDDEN, "로그인 시도 가능 횟수를 초과했습니다. 10분 동안 로그인 시도가 불가능합니다."),

    // Refresh Token 관련 에러 코드
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Refresh Token이 요청에 포함되지 않았습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),

    // 쿠키 관련 에러 코드
    COOKIE_NOT_INCLUDED(HttpStatus.BAD_REQUEST, "쿠키가 요청에 포함되어 있지 않습니다.")
    ;

    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}