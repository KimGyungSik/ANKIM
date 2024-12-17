package shoppingmall.ankim.domain.memberLeave.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LeaveMessageValidator.class)
@Target({ElementType.TYPE}) // 클래스 레벨에서 동작
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLeaveMessage {
    String message() default "기타 사유를 입력해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
