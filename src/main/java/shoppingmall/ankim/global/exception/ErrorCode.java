package shoppingmall.ankim.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    CATEGORY_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "카테고리 이름은 50자 이하로 입력해야 합니다."),
    DUPLICATE_MIDDLE_CATEGORY_NAME(HttpStatus.CONFLICT, "중복된 중분류 이름이 존재합니다."),
    DUPLICATE_SUB_CATEGORY_NAME(HttpStatus.CONFLICT, "해당 중분류 아래에 중복된 소분류 이름이 존재합니다."),
    CHILD_CATEGORY_EXISTS(HttpStatus.CONFLICT, "삭제할 중분류에 소분류가 존재하므로 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;

    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}