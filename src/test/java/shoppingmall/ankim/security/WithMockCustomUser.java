package shoppingmall.ankim.security;


import org.springframework.security.test.context.support.WithSecurityContext;
import shoppingmall.ankim.factory.WithMockCustomUserSecurityContextFactory;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "test@ankim.com"; // 기본 사용자 이름
    String password() default "password";
    String[] roles() default {"ROLE_USER"};     // 기본 역할
}
