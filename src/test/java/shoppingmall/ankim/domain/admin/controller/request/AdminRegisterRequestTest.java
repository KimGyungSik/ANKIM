package shoppingmall.ankim.domain.admin.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AdminRegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효하지 않은 필드는 유효성 검사 오류를 발생시킨다.")
    void invalidRequestValidation() {
        // given
        AdminRegisterRequest request = AdminRegisterRequest.builder()
                .loginId("") // 빈 값
                .pwd("1234") // 비밀번호 형식 오류
                .name("홍 길 동") // 이름 형식 오류
                .email("invalid-email") // 이메일 형식 오류
                .phoneNum("01012345678") // 전화번호 형식 오류
                .birth(LocalDate.of(2125, 1, 1)) // 미래 생년월일
                .gender(null) // 성별 미입력
                .zipCode(null) // 필수값 누락
                .addressMain(null) // 필수값 누락
                .addressDetail("101호")
                .build();

        // when
        Set<ConstraintViolation<AdminRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "올바른 아이디를 입력해주세요.",
                "비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.",
                "이름은 공백, 숫자 없이 2~15자 이내로 입력해야 합니다.",
                "이메일을 입력해주세요.",
                "휴대전화번호 형식이 올바르지 않습니다.",
                "생년월일은 과거 날짜여야 합니다.",
                "성별을 선택해주세요.",
                "우편번호를 입력해주세요.",
                "주소를 선택해주세요."
        );
    }

}