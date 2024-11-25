package shoppingmall.ankim.domain.image.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("파일은 비어 있을 수 없습니다.")
                    .addConstraintViolation();
            return false;
        }

        String fileType = file.getContentType();
        if (fileType == null || !fileType.startsWith("image/")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("이미지 파일 형식만 허용됩니다 (jpg, png 등).")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
