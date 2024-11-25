package shoppingmall.ankim.domain.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.exception.AdminRegistrationException;
import shoppingmall.ankim.domain.admin.repository.AdminRepository;
import shoppingmall.ankim.domain.admin.service.request.AdminIdValidServiceRequest;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;
import shoppingmall.ankim.domain.security.handler.RedisHandler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static shoppingmall.ankim.global.exception.ErrorCode.MEMBER_ID_DUPLICATE;
import static shoppingmall.ankim.global.exception.ErrorCode.REDIS_ID_VALIDATION_SAVE_FAILED;

class AdminServiceTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private RedisHandler redisHandler;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    @DisplayName("중복된 아이디가 존재하면 AdminRegistrationException이 발생한다.")
    void duplicateLoginIdThrowsException() {
        // given
        String duplicateLoginId = "exist";
        AdminIdValidServiceRequest request = AdminIdValidServiceRequest.builder()
                .loginId(duplicateLoginId)
                .build();

        when(adminRepository.existsByLoginId(duplicateLoginId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> adminService.isLoginIdDuplicated(request))
                .isInstanceOf(AdminRegistrationException.class)
                .hasMessageContaining(MEMBER_ID_DUPLICATE.getMessage());

        // Redis 호출이 없는지 확인
        verify(redisHandler, never()).save(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("아이디가 중복되지 않으면 검증 상태를 Redis에 저장한다.")
    void validLoginIdDoesNotThrowException() {
        // given
        String validLoginId = "unique";
        AdminIdValidServiceRequest request = AdminIdValidServiceRequest.builder()
                .loginId(validLoginId)
                .build();

        when(adminRepository.existsByLoginId(validLoginId)).thenReturn(false);

        // when
        adminService.isLoginIdDuplicated(request);

        // then
        // Redis 저장 호출 검증
        verify(redisHandler, times(1))
                .save(eq("admin:validated:loginId:" + validLoginId), eq(validLoginId), eq(1800L));
    }

    @Test
    @DisplayName("Redis에 검증 상태 저장 실패 시 AdminRegistrationException이 발생한다.")
    void redisSaveFailureThrowsException() {
        // given
        String validLoginId = "valid";
        AdminIdValidServiceRequest request = AdminIdValidServiceRequest.builder()
                .loginId(validLoginId)
                .build();

        when(adminRepository.existsByLoginId(validLoginId)).thenReturn(false);
        doThrow(new RuntimeException("Redis 저장 실패"))
                .when(redisHandler).save(anyString(), anyString(), anyLong());

        // when & then
        assertThatThrownBy(() -> adminService.isLoginIdDuplicated(request))
                .isInstanceOf(AdminRegistrationException.class)
                .hasMessageContaining(REDIS_ID_VALIDATION_SAVE_FAILED.getMessage());
    }

    @Test
    @DisplayName("검증되지 않은 아이디로 회원가입 시도 시 예외가 발생한다.")
    void registerWithUnvalidatedIdThrowsException() {
        // given
        String unvalidatedId = "invalidId";
        AdminRegisterServiceRequest request = AdminRegisterServiceRequest.builder()
                .loginId(unvalidatedId)
                .pwd("Password123!")
                .name("홍길동")
                .email("test@example.com")
                .birth(null)
                .gender("M")
                .zipCode(12345)
                .addressMain("서울시 강남구")
                .build();

        when(redisHandler.get("admin:validated:loginId:" + unvalidatedId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> adminService.register(request))
                .isInstanceOf(AdminRegistrationException.class)
                .hasMessageContaining("아이디가 검증되지 않았습니다.");
    }


    @Test
    @DisplayName("회원가입 성공 시 AdminRepository에 데이터가 저장되고 Redis 검증 아이디가 삭제된다.")
    void registerSuccess() {
        // given
        String loginId = "valid";
        String pwd = "ValidPass123!";
        AdminRegisterServiceRequest request = AdminRegisterServiceRequest.builder()
                .loginId(loginId)
                .pwd(pwd)
                .name("관리자")
                .email("admin@example.com")
                .build();

        when(redisHandler.get("admin:validated:loginId:" + loginId)).thenReturn(loginId);
        when(adminRepository.save(any(Admin.class))).thenReturn(mock(Admin.class));

        // when
        adminService.register(request);

        // then
        verify(adminRepository, times(1)).save(argThat(admin ->
                admin.getLoginId().equals(loginId) && admin.getEmail().equals("admin@example.com")));
        verify(redisHandler, times(1)).delete("admin:validated:loginId:" + loginId);
    }
}