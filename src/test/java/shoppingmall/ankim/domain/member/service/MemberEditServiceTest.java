package shoppingmall.ankim.domain.member.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.address.service.request.MemberAddressRegisterServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.dto.MemberAddressResponse;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.terms.dto.TermsAgreeResponse;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.service.TermsService;
import shoppingmall.ankim.factory.MemberFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;
import shoppingmall.ankim.global.util.MaskingUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
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
    private MemberRepository memberRepository;

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    @MockBean
    private TermsService termsService;

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
        PasswordRequest request = PasswordRequest.builder().password(rawPassword).build();

        // Member 생성 및 저장
        Member member = MemberFactory.createSecureMember(em, loginId, rawPassword, bCryptPasswordEncoder);

        // when & then
        assertDoesNotThrow(() -> memberEditService.isValidPassword(loginId, request.toServiceRequest()));
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호가 입력되었을 때 예외를 발생시킨다.")
    void isValidPassword_Fail_InvalidPassword() {
        // given
        String loginId = "secure@example.com";
        String correctPassword = "securePassword123";
        String wrongPassword = "wrongPassword456";
        PasswordRequest request = PasswordRequest.builder().password(wrongPassword).build();


        Member secureMember = MemberFactory.createSecureMember(em, loginId, correctPassword, bCryptPasswordEncoder);

        // when & then
        assertThatThrownBy(() -> memberEditService.isValidPassword(loginId, request.toServiceRequest()))
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
        assertTrue(bCryptPasswordEncoder.matches(newPassword, findMember.getPassword()));
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
                .hasMessageContaining(INVALID_CURRENT_PASSWORD.getMessage());
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

    @Test
    @DisplayName("기본 배송지 및 약관 동의 내역을 포함하여 회원 정보 조회를 성공한다.")
    void getMemberInfo_success() {
        // given
        String loginId = "test@example.com";
        Member member = MemberFactory.createMember(em, loginId);

        // 기존 기본 배송지 설정
        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .baseAddress(MemberAddressRegisterServiceRequest.builder()
                        .zipCode(12345)
                        .addressMain("서울특별시 강남구")
                        .addressDetail("101호")
                        .build()
                        .toBaseAddress())
                .phoneNumber(member.getPhoneNum())
                .defaultAddressYn("Y")
                .build();
        memberAddressRepository.save(memberAddress);

        em.flush();
        em.clear();

        List<TermsAgreeResponse> agreedTerms = List.of(
                new TermsAgreeResponse(1L, 1L, "약관1", "약관내용1", 1, "Y", 2, 1L),
                new TermsAgreeResponse(2L, 2L, "약관2", "약관내용2", 1, "N", 3, 1L)
        );

        given(termsService.getTermsForMember(any(Long.class), eq(TermsCategory.JOIN)))
                .willReturn(agreedTerms);

        // When
        MemberInfoResponse response = memberEditService.getMemberInfo(loginId);

        // Then
        assertThat(response.getNo()).isEqualTo(member.getNo());
        assertThat(response.getLoginId()).isEqualTo(MaskingUtil.maskLoginId(loginId));
        assertThat(response.getName()).isEqualTo(MaskingUtil.maskName(member.getName()));
        assertThat(response.getPhoneNum()).isEqualTo(MaskingUtil.maskPhoneNum(member.getPhoneNum()));
        assertThat(response.getAddress().getAddressMain()).isEqualTo(memberAddress.getBaseAddress().getAddressMain());
        assertThat(response.getAgreedTerms()).hasSize(agreedTerms.size());
        assertThat(response.getAgreedTerms().get(0).getAgreeYn()).isEqualTo("Y");
        assertThat(response.getAgreedTerms().get(1).getAgreeYn()).isEqualTo("N");
    }

    @Test
    @DisplayName("기본 배송지가 없는 경우에도 회원 정보 조회를 성공한다.")
    void getMemberInfo_noAddress() {
        // given
        String loginId = "test@example.com";
        Member member = MemberFactory.createMember(em, loginId);

        em.flush();
        em.clear();

        List<TermsAgreeResponse> agreedTerms = List.of(
                new TermsAgreeResponse(1L, 1L, "약관1", "약관내용1", 1, "Y", 2, 1L),
                new TermsAgreeResponse(2L, 2L, "약관2", "약관내용2", 1, "N", 3, 1L)
        );

        given(termsService.getTermsForMember(any(Long.class), eq(TermsCategory.JOIN)))
                .willReturn(agreedTerms);

        // When
        MemberInfoResponse response = memberEditService.getMemberInfo(loginId);

        // Then
        assertThat(response.getNo()).isEqualTo(member.getNo());
        assertThat(response.getLoginId()).isEqualTo(MaskingUtil.maskLoginId(loginId));
        assertThat(response.getName()).isEqualTo(MaskingUtil.maskName(member.getName()));
        assertThat(response.getPhoneNum()).isEqualTo(MaskingUtil.maskPhoneNum(member.getPhoneNum()));
        assertThat(response.getAddress()).isNull();
        assertThat(response.getAgreedTerms()).hasSize(agreedTerms.size());
        assertThat(response.getAgreedTerms().get(0).getAgreeYn()).isEqualTo("Y");
        assertThat(response.getAgreedTerms().get(1).getAgreeYn()).isEqualTo("N");
    }
}