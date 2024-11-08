package shoppingmall.ankim.domain.email.service;

import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;


class MailServiceTest {

    @Captor
    ArgumentCaptor<String> contentCaptor; // 메서드 호출시 인수 캡쳐

    @Mock
    private JavaMailSender javaMailSender; // 가짜로 메일을 보내는 객체 생성(모의 객체)

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks // 인터페이스 주입 X
    private MailServiceImpl mailService; // 모의 객체를 MailService에 주입

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 모의 객체와 주입된 객체를 초기화
    }
    /*
    * 테스트 및 개발 순서
    * 1. 인증 번호 생성
    * 2. 이메일 발송하기 위해서 MimeMessage 객체 생성
    * 3. MimeMessage 객체를 통해서 이메일 전송
    * 4. 인증번호 일치하는지 검증
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
        String expectedSubject = "이메일 인증";
        String expectedSender = "admin@ankim.com";

        // when
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage); // JavaMailSender의 createMimeMessage가 mock된 mimeMessage 반환
        MimeMessage message = mailService.createMail(email);

        // then: 각 설정 값 검증
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(mimeMessage, atLeastOnce()).setFrom(addressCaptor.capture()); // setForm에 전달되는 값을 캡쳐
        assertThat(addressCaptor.getValue().toString()).isEqualTo(expectedSender); // 보내는 사람 이메일과 일치하는지 확인

        verify(mimeMessage, atLeastOnce()).setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email)); // 이메일 설정이 잘 되었는지 확인

        // then: 메일 본문 내용 확인
        verify(mimeMessage).setContent(contentCaptor.capture(), eq("text/html;charset=UTF-8"));
        assertThat(contentCaptor.getValue()).contains("<h1>인증번호:"); // HTML 내용이 예상한 인증번호 형식과 일치하는지 확인
    }

    @Test
    @DisplayName("이메일 전송 기능 테스트")
    public void test3() throws Exception {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("인증번호 검증 테스트")
    public void test4() throws Exception {
        // given

        // when

        // then

    }

}