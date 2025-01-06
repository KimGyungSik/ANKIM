package shoppingmall.ankim.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.email.exception.MailSendException;
import shoppingmall.ankim.domain.email.handler.MailVerificationHandler;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import static shoppingmall.ankim.global.exception.ErrorCode.MAIL_SEND_FAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final MailVerificationHandler mailVerificationHandler;

    // application-email.yml의 발신자 이메일 주소 주입
    @Value("${mail.username}")
    private String fromAddress;

    // 암호화 문자
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 인증번호 길이
    private static final int CODE_LENGTH = 6;
    // 난수값 생성
    private static SecureRandom random = new SecureRandom();

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
            helper.setText("<h1>인증번호: " + code + "</h1>", true); // HTML 형식 메시지

            // Redis에 인증 코드 저장
            mailVerificationHandler.saveVerificationCode(loginId, code);
            // 실패 횟수 초기화
            mailVerificationHandler.resetFailCount(loginId); // 실패 횟수 초기화

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException(MAIL_SEND_FAIL);
        }

        return message;
    }

    // 이메일 전송
    @Override
    @Async("mailTaskExecutor") // 특정 Executor 지정 및 메서드 비동기 실행
    public void sendMail(MimeMessage message) {
//        long start = System.currentTimeMillis();
        javaMailSender.send(message);
//        long end = System.currentTimeMillis();
//        log.info("이메일 전송시간 {} ms", (end - start));
    }

    @Override
    public Count verifyCode(String loginId, String inputCode) {
//        String storedCode = verificationCodes.get(loginId);
        String storedCode = mailVerificationHandler.getVerificationCode(loginId);

        // 실패 횟수 확인
        int failCount = mailVerificationHandler.incrementFailCount(loginId);
        log.info("실패 횟수 : {}", failCount);

        // 실패 횟수 초과 처리
        if (failCount >= 3) {
            return Count.RETRY; // 실패 횟수 3회 초과 시 "RETRY" 반환
        }

        if (storedCode != null && storedCode.equals(inputCode)) {
//            failCounts.remove(loginId); // 성공 시 실패 횟수 초기화
            mailVerificationHandler.setVerified(loginId); // 인증 성공 처리
            return Count.SUCCESS;
        }

        return Count.FAIL;
    }

}

