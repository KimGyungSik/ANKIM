package shoppingmall.ankim.domain.email.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MailRequest {

    private String email; // 받는사람

    @NotBlank(message = "올바른 인증번호를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$", message = "올바른 인증번호를 입력해주세요.")
    private String verificationCode; // 인증번호

    @Builder
    public MailRequest(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
}
