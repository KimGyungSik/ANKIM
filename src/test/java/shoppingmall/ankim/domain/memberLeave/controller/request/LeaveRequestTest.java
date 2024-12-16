package shoppingmall.ankim.domain.memberLeave.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LeaveRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    @DisplayName("유효한 LeaveRequest 객체는 유효성 검사를 통과한다.")
    void validLeaveRequest_passesValidation() {
        // given
        LeaveRequest request = LeaveRequest.builder()
                .leaveReasonNo(6L)
                .leaveReason("기타")
                .leaveMessage("기타 사유입니다.")
                .agreeYn("Y")
                .password("password123")
                .build();

        // when
        Set<ConstraintViolation<LeaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("기타 사유 선택 시 leaveMessage가 없으면 유효성 검사에 실패한다.")
    void leaveMessageIsRequired_whenReasonIsOther() {
        // given
        LeaveRequest request = LeaveRequest.builder()
                .leaveReasonNo(6L)
                .leaveReason("기타")
                .leaveMessage(null) // 기타 사유 없음
                .agreeYn("Y")
                .password("password123")
                .build();

        // when
        Set<ConstraintViolation<LeaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("기타 사유를 입력해주세요."));
    }

    @Test
    @DisplayName("기타 외의 사유 선택 시 사유가 없어도 유효성 검사를 통과한다.")
    void leaveMessageIsNotRequired_whenReasonIsNotOther() {
        // given
        LeaveRequest request = LeaveRequest.builder()
                .leaveReasonNo(1L) // 기타 외의 사유
                .leaveReason("탈퇴 후 재가입을 위해서")
                .leaveMessage(null)
                .agreeYn("Y")
                .password("password123")
                .build();

        // when
        Set<ConstraintViolation<LeaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("탈퇴 동의 여부인 agreeYn이 비어있으면 유효성 검사를 실패한다.")
    void agreeYnIsRequired() {
        // given
        LeaveRequest request = LeaveRequest.builder()
                .leaveReasonNo(1L)
                .leaveReason("탈퇴 후 재가입을 위해서")
                .leaveMessage(null)
                .agreeYn(null) // 동의 여부 없음
                .password("password123")
                .build();

        // when
        Set<ConstraintViolation<LeaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("탈퇴 유의 사항을 읽으신 후 체크해주세요."));
    }

    @Test
    @DisplayName("password가 비어있으면 Validation에 실패한다")
    void passwordIsRequired() {
        // given
        LeaveRequest request = LeaveRequest.builder()
                .leaveReasonNo(1L)
                .leaveReason("탈퇴 후 재가입을 위해서")
                .leaveMessage(null)
                .agreeYn("Y")
                .password(null) // 비밀번호 없음
                .build();

        // when
        Set<ConstraintViolation<LeaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("비밀번호를 정확하게 입력해주세요."));
    }

}