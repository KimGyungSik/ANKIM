package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ImageLimitExceededException extends CustomLogicException {
    public ImageLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
