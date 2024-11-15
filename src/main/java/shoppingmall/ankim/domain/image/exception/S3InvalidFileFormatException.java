package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class S3InvalidFileFormatException extends CustomLogicException {
    public S3InvalidFileFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
