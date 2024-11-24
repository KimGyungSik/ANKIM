package shoppingmall.ankim.domain.login.service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.login.entity.member.LoginType;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class LoginServiceRequest {

    private String loginId; // 아이디(이메일)
    private String pwd; // 비밀번호
    private LoginType loginType; // 로그인 타입 (EMAIL)
    private LocalDateTime loginTime; // 로그인 시각
    private String autoLogin; // 자동로그인 여부

    @Builder
    public LoginServiceRequest(String loginId, String pwd, LoginType loginType, LocalDateTime loginTime, String autoLogin) {
        this.loginId = loginId;
        this.pwd = pwd;
        this.loginType = loginType == null? LoginType.EMAIL : loginType;
        this.loginTime = loginTime == null? LocalDateTime.now() : loginTime;
        this.autoLogin = autoLogin;
    }
}
