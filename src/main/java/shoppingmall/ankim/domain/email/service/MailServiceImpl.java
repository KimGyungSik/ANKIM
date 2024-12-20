package shoppingmall.ankim.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.email.exception.MailSendException;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static shoppingmall.ankim.global.exception.ErrorCode.MAIL_SEND_FAIL;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    // application-email.yml의 발신자 이메일 주소 주입
    @Value("${mail.username}")
    private String fromAddress;

    // 암호화 문자
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 인증번호 길이
    private static final int CODE_LENGTH = 6;
    // 난수값 생성
    private static SecureRandom random = new SecureRandom();

    // 임시로 생성된 인증번호를 저장
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>(); // 동시성 지원

    // 실패 횟수를 저장
    private final Map<String, Integer> failCounts = new ConcurrentHashMap<>();

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
    public MimeMessage createMail(String loginId, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(new InternetAddress(fromAddress, "Ankim Admin")); // 보내는 사람
            helper.setTo(loginId); // 받는 사람
            helper.setSubject("Ankim 이메일 인증");
            verificationCodes.put(loginId, code); // 생성된 인증번호 저장
            failCounts.put(loginId, 0); // 실패 횟수 초기화
            helper.setText("<h1>인증번호: " + code + "</h1>", true); // HTML 형식 메시지
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException(MAIL_SEND_FAIL);
        }

        return message;
    }

    // 이메일 전송
    @Override
    public void sendMail(MimeMessage message) {
        javaMailSender.send(message);
    }

    @Override
    public Count verifyCode(String loginId, String inputCode) {
        String storedCode = verificationCodes.get(loginId);

        // 실패 횟수 초과 처리
        if (failCounts.getOrDefault(loginId, 0) >= 3) {
            return Count.RETRY; // 실패 횟수 3회 초과 시 "RETRY" 반환
        }

        if (storedCode != null && storedCode.equals(inputCode)) {
            failCounts.remove(loginId); // 성공 시 실패 횟수 초기화
            return Count.SUCCESS;
        }

        failCounts.put(loginId, failCounts.getOrDefault(loginId, 0) + 1);
        return Count.FAIL;
    }

}

