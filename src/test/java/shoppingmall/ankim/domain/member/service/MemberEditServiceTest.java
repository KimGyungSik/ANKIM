package shoppingmall.ankim.domain.member.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberFactory;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@SpringBootTest
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
@Transactional
class MemberEditServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private MemberEditService memberEditService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private S3Service s3Service;
    @Autowired
    private MemberHistoryRepository memberHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 비밀번호가 입력되었을 때 성공한다.")
    void isValidPassword_Success() {
        // given
        String loginId = "test@example.com";
        String rawPassword = "password123";

        // Member 생성 및 저장
        Member member = MemberFactory.createSecureMember(em, loginId, rawPassword, bCryptPasswordEncoder);

        // when & then
        assertDoesNotThrow(() -> memberEditService.isValidPassword(loginId, rawPassword));
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호가 입력되었을 때 예외를 발생시킨다.")
    void isValidPassword_Fail_InvalidPassword() {
        // given
        String loginId = "secure@example.com";
        String correctPassword = "securePassword123";
        String wrongPassword = "wrongPassword456";

        Member secureMember = MemberFactory.createSecureMember(em, loginId, correctPassword, bCryptPasswordEncoder);

        // when & then
        assertThatThrownBy(() -> memberEditService.isValidPassword(loginId, wrongPassword))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessageContaining(INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("기존 비밀번호, 새로운 비밀번호 ,검증할 비밀번호를 올바르게 입력하는 경우 비밀번호 변경을 성공한다.")
    void changePassword_Success() {
        // given
        String loginId = "test@example.com";
        String rawPassword = "password123";
        String newPassword = "newPassword123!";

        // Member 생성 및 저장
        Member member = MemberFactory.createSecureMember(em, loginId, rawPassword, bCryptPasswordEncoder);

        ChangePasswordServiceRequest request = ChangePasswordServiceRequest.builder()
                .oldPassword(rawPassword)
                .newPassword(newPassword)
                .confirmPassword(newPassword)
                .build();

        String encodeNewPassword = bCryptPasswordEncoder.encode(newPassword);

        // when
        assertDoesNotThrow(() -> memberEditService.changePassword(loginId, request));

        // then
        Member findMember = memberRepository.findByLoginId(loginId);
        // matches()를 사용하여 새 비밀번호가 올바르게 저장되었는지 검증
        assertTrue(bCryptPasswordEncoder.matches(newPassword, findMember.getPwd()));
    }

    @Test
    @DisplayName("기존 비밀번호를 잘못 입력한 경우 비밀번호 변경을 실패한다.")
    void changePassword_Fail_InvalidOldPassword() {
        // given
        String loginId = "test@example.com";
        String rawPassword = "password123";
        String wrongOldPassword = "wrongPassword456";
        String newPassword = "newPassword123!";

        // Member 생성 및 저장
        Member member = MemberFactory.createSecureMember(em, loginId, rawPassword, bCryptPasswordEncoder);

        ChangePasswordServiceRequest request = ChangePasswordServiceRequest.builder()
                .oldPassword(wrongOldPassword)
                .newPassword(newPassword)
                .confirmPassword(newPassword)
                .build();

        // when & then
        assertThatThrownBy(() -> memberEditService.changePassword(loginId, request))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessageContaining(CURRENT_PASSWORD_INVALID.getMessage());
    }

    @Test
    @DisplayName("기존 비밀번호와 새로운 비밀번호가 동일한 경우 비밀번호 변경을 실패한다.")
    void changePassword_Fail_NewPasswordSameAsOld() {
        // given
        String loginId = "test@example.com";
        String rawPassword = "password123"; // 기존 비밀번호와 동일

        // Member 생성 및 저장
        Member member = MemberFactory.createSecureMember(em, loginId, rawPassword, bCryptPasswordEncoder);

        ChangePasswordServiceRequest request = ChangePasswordServiceRequest.builder()
                .oldPassword(rawPassword)
                .newPassword(rawPassword)
                .confirmPassword(rawPassword)
                .build();

        // when & then
        assertThatThrownBy(() -> memberEditService.changePassword(loginId, request))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessageContaining(PASSWORD_SAME_AS_OLD.getMessage());
    }

    @Test
    @DisplayName("새로운 비밀번호와 검증할 비밀번호가 일치하지 않는 경우 비밀번호 변경을 실패한다.")
    void changePassword_Fail_NewPasswordConfirmationMismatch() {
        // given
        String loginId = "test@example.com";
        String rawPassword = "password123";
        String newPassword = "newPassword123!";
        String confirmPassword = "mismatchPassword123!"; // 불일치

        // Member 생성 및 저장
        Member member = MemberFactory.createSecureMember(em, loginId, rawPassword, bCryptPasswordEncoder);

        ChangePasswordServiceRequest request = ChangePasswordServiceRequest.builder()
                .oldPassword(rawPassword)
                .newPassword(newPassword)
                .confirmPassword(confirmPassword)
                .build();

        // when & then
        assertThatThrownBy(() -> memberEditService.changePassword(loginId, request))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessageContaining(PASSWORD_CONFIRMATION_MISMATCH.getMessage());
    }
}