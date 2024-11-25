package shoppingmall.ankim.domain.admin.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AdminIdValidRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("아이디가 숫자로 시작하면 유효성 검사가 실패한다.")
    void invalidLoginIdStartsWithNumber() {
        // given
        AdminIdValidRequest request = AdminIdValidRequest.builder()
                .loginId("123abc")
                .build();

        // when
        Set<ConstraintViolation<AdminIdValidRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("아이디는 3~16자의 소문자와 숫자만 사용하며 숫자로 시작할 수 없습니다."));
    }

    @Test
    @DisplayName("아이디가 대문자를 포함하면 유효성 검사가 실패한다.")
    void invalidLoginIdWithUpperCase() {
        // given
        AdminIdValidRequest request = AdminIdValidRequest.builder()
                .loginId("abcD123")
                .build();

        // when
        Set<ConstraintViolation<AdminIdValidRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("아이디는 3~16자의 소문자와 숫자만 사용하며 숫자로 시작할 수 없습니다."));
    }

    @Test
    @DisplayName("아이디 길이가 3자 미만이면 유효성 검사가 실패한다.")
    void invalidLoginIdTooShort() {
        // given
        AdminIdValidRequest request = AdminIdValidRequest.builder()
                .loginId("ab")
                .build();

        // when
        Set<ConstraintViolation<AdminIdValidRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("아이디는 3~16자의 소문자와 숫자만 사용하며 숫자로 시작할 수 없습니다."));
    }

    @Test
    @DisplayName("유효하게 입력한 아이디는 유효성 검사를 통과한다.")
    void validLoginId() {
        // given
        AdminIdValidRequest request = AdminIdValidRequest.builder()
                .loginId("abc123")
                .build();

        // when
        Set<ConstraintViolation<AdminIdValidRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
}