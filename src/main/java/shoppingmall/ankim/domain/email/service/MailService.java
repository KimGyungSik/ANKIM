package shoppingmall.ankim.domain.email.service;

import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    // 랜덤으로 영문 대소문자 + 숫자로 구성된 인증번호 생성
    String generateCode();
    // 이메일 메시지 생성
    MimeMessage createMail(String loginId, String code);
    // 이메일 전송
//    void sendMail(MimeMessage message);
    CompletableFuture<Void> sendMail(MimeMessage message);
    // 사용자가 입력한 인증번호와 생성된 인증번호 비교
    Count verifyCode(String loginId, String inputCode);

}
