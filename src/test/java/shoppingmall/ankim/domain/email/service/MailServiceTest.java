package shoppingmall.ankim.domain.email.service;

import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.handler.MailVerificationHandler;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.global.config.TestMailAsyncConfig;

import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;


@Import(TestMailAsyncConfig.class)
@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class MailServiceTest {

    @Captor
    ArgumentCaptor<String> contentCaptor; // 메서드 호출시 인수 캡쳐

    @MockBean
    private MailVerificationHandler mailVerificationHandler;

    @MockBean
    private JavaMailSender javaMailSender; // @MockBean으로 명확히 설정

    private MimeMessage mimeMessage;

    private Validator validator;

    @Autowired // 인터페이스 주입 X
    private MailService mailService; // 모의 객체를 MailService에 주입

    @MockBean
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
        Mockito.reset(javaMailSender); // 이전 테스트의 영향을 제거
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        mimeMessage = mock(MimeMessage.class); // MimeMessage 명확하게 초기화
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // MimeMessageHelper가 내부적으로 호출하는 메서드를 명확히 처리
        try {
            doNothing().when(mimeMessage).setFrom(any(Address.class));
            doNothing().when(mimeMessage).setRecipient(any(MimeMessage.RecipientType.class), any(Address.class));
            doNothing().when(mimeMessage).setContent(anyString(), eq("text/html;charset=UTF-8"));
            doNothing().when(mimeMessage).setSubject(anyString());
        } catch (Exception e) {
            throw new RuntimeException("MimeMessage Mock 설정 오류", e);
        }
    }

    /*
    * 테스트 및 개발 순서
    * 1. 인증 번호 생성
    * 2. 이메일 발송하기 위해서 MimeMessage 객체 생성
    * 3. MimeMessage 객체를 통해서 이메일 전송
    * 4. 사용자가 입력한 인증번호가 일치하는지 검증
    * */

    @Test
    @DisplayName("인증 번호를 생성할 수 있다.")
    public void generateCodeTest() throws Exception {
        // given : 이메일 보낼 주소를 입력하였다고 가정한다.
        // when : 인증번호 생성한다.
        String code = mailService.generateCode();

        // then : 생성한 인증번호가 6자리인지, 알파벳 대소문자와 숫자로 구성되었는지 확인한다.
        assertThat(code.length()).isEqualTo(6);
        assertThat(Pattern.matches("^[a-zA-Z0-9]+$", code)).isTrue();
    }


    @Test
    @DisplayName("이메일 생성 시 보내는 사람, 받는 사람, 제목, 내용이 올바르게 설정되는지 검증한다.")
    public void createMailTest() throws Exception {
        // given
        String email = "test@example.com";
        String code = mailService.generateCode();

        // when
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage); // JavaMailSender의 createMimeMessage가 mock된 mimeMessage 반환
        MimeMessage message = mailService.createMail(email, code);

        // then: 각 설정 값 검증
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(mimeMessage, atLeastOnce()).setFrom(addressCaptor.capture()); // setForm에 전달되는 값을 캡쳐

        verify(mimeMessage, atLeastOnce()).setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email)); // 이메일 설정이 잘 되었는지 확인

        // then: 메일 본문 내용 확인
        verify(mimeMessage).setContent(contentCaptor.capture(), eq("text/html;charset=UTF-8"));
        assertThat(contentCaptor.getValue()).contains("<h1>인증번호:"); // HTML 내용이 예상한 인증번호 형식과 일치하는지 확인
    }

    @Test
    @DisplayName("인증번호가 수신자 이메일에 잘 전송되는지 확인한다.")
    public void mailSend_SUCCESS() throws Exception {
        // given
        String email = "test@example.com";
        String code = mailService.generateCode();
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage); // JavaMailSender의 createMimeMessage가 mock된 mimeMessage 반환
        MimeMessage mimeMessage = mailService.createMail(email, code); // 이메일 객체 생성

        // when
        CompletableFuture<Void> future = mailService.sendMail(mimeMessage); // 비동기 메서드 호출
        future.join(); // 작업 완료 대기

        // then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));  // JavaMailSender의 send 메서드가 호출되었는지 확인
    }

    @Test
    @DisplayName("사용자가 입력한 인증번호가 서버의 생성된 인증번호와 일치하는 경우 일치한다는 메시지가 전달되는지 확인한다.")
    public void verifyCodeSuccessTest() {
        // given
        String email = "test@example.com";
        String code = mailService.generateCode();

        // saveVerificationCode 호출 시 동작하도록 EmailVerificationHandler Mock 설정
        doNothing().when(mailVerificationHandler).saveVerificationCode(email, code);
        when(mailVerificationHandler.getVerificationCode(email)).thenReturn(code);

        // 인증번호 저장
        mailService.createMail(email, code);

        MailRequest mailRequest = new MailRequest(email, code);

        // when: 올바른 인증번호 입력
        Count result = mailService.verifyCode(mailRequest.getLoginId(), mailRequest.getVerificationCode());

        // then
        assertThat(result).isEqualTo(Count.SUCCESS); // 인증 성공 확인
        verify(mailVerificationHandler, times(1)).getVerificationCode(email);
    }


    @Test
    @DisplayName("사용자가 입력한 인증번호가 서버의 생성된 인증번호와 일치하지 않는 경우 일치하지 않는다는 메시지가 전달되는지 확인한다.")
    public void verifyCodeFailTest() {
        // given
        String email = "test@example.com";
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage); // JavaMailSender의 createMimeMessage가 mock된 mimeMessage 반환

        String code = mailService.generateCode();
        mailService.createMail(email, code); // 내부적으로 인증번호를 저장했다고 가정

        MailRequest mailRequest = new MailRequest(email, "");

        // 유효성 검사
        Set<ConstraintViolation<MailRequest>> violations = validator.validate(mailRequest);

        // when
        Count isValid = mailService.verifyCode(mailRequest.getLoginId(), mailRequest.getVerificationCode());

        // then
        assertThat(violations).isNotEmpty(); // 유효성 검사 오류가 있는지 확인
        assertThat(isValid).isEqualTo(Count.FAIL);     // 인증번호 검증 결과가 틀렸는지 확인

        System.out.println("violations = " + violations);
    }

    @Test
    @DisplayName("사용자가 인증번호를 3회 이상 잘못 입력한 경우 RETRY 메시지가 전달되는지 확인한다.")
    public void verifyCodeRetryAfterThreeFailuresTest() {
        // given
        String email = "test@example.com";
        String correctCode = mailService.generateCode();

        // 실패 횟수를 추적하는 변수
        final int[] failCount = {0};

        // EmailVerificationHandler 동작 설정
        doNothing().when(mailVerificationHandler).saveVerificationCode(email, correctCode);
        when(mailVerificationHandler.getVerificationCode(email)).thenReturn(correctCode);

        // 실패 횟수 증가 시 동작 설정
        // Mock 객체는 상태를 저장하지 않기 때문에 실패 횟수를 추적하기 위해 int[] failCount 배열을 사용
        // thenAnswer : 입력값을 기반으로 다른 값을 반환
        when(mailVerificationHandler.incrementFailCount(email)).thenAnswer(invocation -> ++failCount[0]);

        // when: 2회 잘못된 인증번호 입력
        for (int i = 0; i < 2; i++) {
            Count result = mailService.verifyCode(email, "wrongCode");
            assertThat(result).isEqualTo(Count.FAIL); // 실패인 경우
        }

        // 3번째 실패 시 RETRY 응답 확인
        Count result = mailService.verifyCode(email, "wrongCode");

        // then
        assertThat(result).isEqualTo(Count.RETRY); // 4번째 실패는 RETRY 반환
    }

    @Test
    @DisplayName("3회 인증번호 틀린 후 새 인증번호 요청하고, 올바르게 입력하면 성공 메시지가 전달되는지 확인한다.")
    public void verifyCodeAfterRetryWithNewCodeTest() {
        // given
        String email = "test@example.com";
        String correctCode = mailService.generateCode();

        // emailVerificationHandler의 동작 설정
        doNothing().when(mailVerificationHandler).saveVerificationCode(email, correctCode);
        when(mailVerificationHandler.getVerificationCode(email))
                .thenReturn(correctCode)
                .thenReturn(correctCode); // 새 코드 요청 후에도 반환값 설정

        // 기존 코드 검증 실패 시 3번 시도
        for (int i = 0; i < 3; i++) {
            mailService.verifyCode(email, "wrongCode");
        }

        // 새로운 인증번호 발급 요청
        String newCode = mailService.generateCode();
        doNothing().when(mailVerificationHandler).saveVerificationCode(email, newCode);
        when(mailVerificationHandler.getVerificationCode(email)).thenReturn(newCode);

        // when: 새로운 코드로 인증 성공 시도
        Count result = mailService.verifyCode(email, newCode);

        // then
        assertThat(result).isEqualTo(Count.SUCCESS);
    }

}