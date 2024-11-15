package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class S3FileDeletionException extends CustomLogicException {
    public S3FileDeletionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
