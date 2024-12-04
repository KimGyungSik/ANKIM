package shoppingmall.ankim.domain.member.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.admin.exception.AdminRegistrationException;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberFactory;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@SpringBootTest
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
@Transactional
class MemberMyPageServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private MemberMyPageService memberMyPageService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private S3Service s3Service;

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
        assertDoesNotThrow(() -> memberMyPageService.isValidPassword(loginId, rawPassword));
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
        assertThatThrownBy(() -> memberMyPageService.isValidPassword(loginId, wrongPassword))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessageContaining(INVALID_PASSWORD.getMessage());
    }
}