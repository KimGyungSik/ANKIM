package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DetailImageRequiredException extends CustomLogicException {
    public DetailImageRequiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
