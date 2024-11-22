package shoppingmall.ankim.domain.address.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AddressRegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("우편번호를 입력하지 않으면 오류가 발생한다.")
    public void invalidZipCode() {
        // given
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .zipCode(null) // 빈 값
                .addressMain("서울특별시 강남구")
                .addressDetail("101호")
                .build();

        // when
        Set<ConstraintViolation<AddressRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty(); // 유효성 검사 오류가 발생해야 함
        assertThat(violations).anyMatch(v -> v.getMessage().equals("우편번호를 입력해주세요.")); // 오류 메시지 확인
    }

    @Test
    @DisplayName("주소를 선택하지 않으면 오류가 발생한다.")
    public void invalidAddressMain() {
        // given
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .zipCode(12345)
                .addressMain("") // 빈 값
                .addressDetail("101호")
                .build();

        // when
        Set<ConstraintViolation<AddressRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty(); // 유효성 검사 오류가 발생해야 함
        assertThat(violations).anyMatch(v -> v.getMessage().equals("주소를 선택해주세요.")); // 오류 메시지 확인
    }


    @Test
    @DisplayName("여러 필드에서 유효성 검사가 실패하면 각각에 대해 오류 메시지가 발생한다.")
    public void multipleInvalidFields() {
        // given
        AddressRegisterRequest request = AddressRegisterRequest.builder()
                .zipCode(null) // 빈 값
                .addressMain("") // 빈 값
                .addressDetail("101호")
                .build();

        // when
        Set<ConstraintViolation<AddressRegisterRequest>> violations = validator.validate(request);

        for (ConstraintViolation<AddressRegisterRequest> violation : violations) {
            System.out.println("field : " + violation.getPropertyPath());
            System.out.println("error : " + violation.getMessage());
        }

        // then
        assertThat(violations).hasSize(2); // 예외가 발생하는 필드 개수 확인
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "우편번호를 입력해주세요.",
                        "주소를 선택해주세요."
                );
    }

    @Test
    @DisplayName("유효하지 않은 필드는 유효성 검사 오류를 발생시킨다.")
    void invalidRequestValidation() {
        // given
        AdminRegisterServiceRequest request = AdminRegisterServiceRequest.builder()
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
        Set<ConstraintViolation<AdminRegisterServiceRequest>> violations = validator.validate(request);

        // then
        Assertions.assertThat(violations).isNotEmpty();
        Assertions.assertThat(violations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
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