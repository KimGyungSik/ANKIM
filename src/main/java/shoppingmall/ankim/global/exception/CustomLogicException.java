package shoppingmall.ankim.global.exception;

import lombok.Getter;

@Getter
public class CustomLogicException extends RuntimeException{
    private final ErrorCode errorCode;
    public CustomLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
