package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ThumbnailImageRequiredException extends CustomLogicException {
    public ThumbnailImageRequiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
