package shoppingmall.ankim.domain.image.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class FileUploadException extends CustomLogicException {
    public FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
