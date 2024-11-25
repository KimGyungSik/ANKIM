package shoppingmall.ankim.domain.image.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
@Documented
public @interface ValidImageFile {
    String message() default "유효하지 않은 파일입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

