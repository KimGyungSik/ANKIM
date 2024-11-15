package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class S3FileUploadException extends CustomLogicException {
    public S3FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
