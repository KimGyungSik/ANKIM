package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class FileNotFoundException extends CustomLogicException {
    public FileNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
