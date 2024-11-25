package shoppingmall.ankim.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

// 메일 전송을 위한 JavaMailSender 설정
@Configuration
public class MailConfig {
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        // Gmail SMTP 서버 사용위한 설정
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // SMTP 서버 설정
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // 이메일 전송 프로토콜 지정
        props.put("mail.smtp.auth", "true"); // SMTP 인증 사용 설정
        props.put("mail.smtp.starttls.enable", "true"); // TLS 활성화
        props.put("mail.debug", "true"); // 이메일 전송 관련 로그를 출력

        return mailSender;
    }
}
