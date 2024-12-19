package shoppingmall.ankim.domain.member.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MemberRegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("비밀번호 형식이 올바르지 않으면 오류가 발생한다.")
    void invalidPassword() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .pwd("1234") // 비밀번호 형식이 틀림
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .build();

        // when
        Set<ConstraintViolation<MemberRegisterRequest>> violations = validator.validate(request);

        // then
        System.out.println("violations = " + violations);
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."));
    }

    @Test
    @DisplayName("이름 형식이 올바르지 않으면 오류가 발생한다.")
    void invalidName() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .pwd("ValidPass123!")
                .name("홍 길 동") // 이름 형식이 틀림
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .build();

        // when
        Set<ConstraintViolation<MemberRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("이름은 공백, 숫자 없이 2~15자 이내로 입력해야 합니다."));
    }

    @Test
    @DisplayName("휴대전화번호 형식이 올바르지 않으면 오류가 발생한다.")
    void invalidPhoneNumber() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .pwd("ValidPass123!")
                .name("홍길동")
                .phoneNum("01012345678") // 전화번호 형식이 틀림
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .build();

        // when
        Set<ConstraintViolation<MemberRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("휴대전화번호 형식이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("생년월일 형식이 올바르지 않으면 오류가 발생한다.")
    void invalidBirthDate() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .pwd("ValidPass123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(2025, 1, 1)) // 미래 날짜로 설정하여 오류 발생
                .gender("M")
                .build();

        // when
        Set<ConstraintViolation<MemberRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("생년월일은 과거 날짜여야 합니다."));
    }

    @Test
    @DisplayName("성별을 입력하지 않으면 오류가 발생한다.")
    void nullGender() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .pwd("ValidPass123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(null) // 성별을 입력하지 않음
                .build();

        // when
        Set<ConstraintViolation<MemberRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("성별을 선택해주세요."));
    }

    @Test
    @DisplayName("여러 필드에서 유효성 검사가 실패하면 각각에 대해 오류 메시지가 발생한다.")
    void multipleInvalidFields() {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .pwd("short") // 비밀번호 형식 오류
                .name("홍 길 동") // 이름 형식 오류
                .phoneNum("01012345678") // 전화번호 형식 오류
                .birth(LocalDate.of(2025, 1, 1)) // 미래 생년월일
                .gender(null) // 성별 미입력
                .build();

        // when
        Set<ConstraintViolation<MemberRegisterRequest>> violations = validator.validate(request);

        for (ConstraintViolation<MemberRegisterRequest> violation : violations) {
            System.out.println("field : " + violation.getPropertyPath());
            System.out.println("error : " + violation.getMessage());
        }

        // then
        assertThat(violations).hasSize(5); // 예외가 발생하는 필드 개수 확인
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.",
                        "이름은 공백, 숫자 없이 2~15자 이내로 입력해야 합니다.",
                        "휴대전화번호 형식이 올바르지 않습니다.",
                        "생년월일은 과거 날짜여야 합니다.",
                        "성별을 선택해주세요."
                );
    }
}