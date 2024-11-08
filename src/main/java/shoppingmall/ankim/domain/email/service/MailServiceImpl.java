package shoppingmall.ankim.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.security.SecureRandom;

import static shoppingmall.ankim.global.exception.ErrorCode.MAIL_SEND_FAIL;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    // 암호화 문자
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 인증번호 길이
    private static final int CODE_LENGTH = 6;
    // 난수값 생성
    private static SecureRandom random = new SecureRandom();

    // JavaMailSender 주입
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // 랜덤으로 영문 대소문자 + 숫자로 구성된 인증번호 생성
    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }

    // 이메일 메시지 생성
    @Override
    public MimeMessage createMail(String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom("admin@ankim.com"); // 보내는 사람
            helper.setTo(email); // 받는 사람
            helper.setSubject("이메일 인증");
            String code = generateCode();
            helper.setText("<h1>인증번호: " + code + "</h1>", true); // HTML 형식 메시지
        } catch (MessagingException e) {
            throw new CustomLogicException(MAIL_SEND_FAIL);
        }

        return message;
    }


}
