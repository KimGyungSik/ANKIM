package shoppingmall.ankim.domain.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MailDto {
    private String email; // 받는사람
    private String verificationCode; // 인증번호

    public MailDto(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
}
