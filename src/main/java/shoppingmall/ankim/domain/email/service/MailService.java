package shoppingmall.ankim.domain.email.service;

import jakarta.mail.internet.MimeMessage;

import java.security.SecureRandom;

public interface MailService {
    // 랜덤으로 영문 대소문자 + 숫자로 구성된 인증번호 생성
    String generateCode();
    // 이메일 메시지 생성
    MimeMessage createMail(String email);

}
