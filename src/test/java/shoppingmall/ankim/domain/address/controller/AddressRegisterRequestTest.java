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

}