package shoppingmall.ankim.domain.login.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.login.entity.member.LoginType;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일을 입력해주세요.") // null 과 "" 과 " " 모두 허용하지 않는다.
    private String loginId; // 아이디(이메일)

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String pwd; // 비밀번호

    private LoginType loginType; // 로그인 타입 (EMAIL)
    private LocalDateTime loginTime; // 로그인 시각
    private boolean autoLogin; // 자동 로그인 여부

    @Builder
    public LoginRequest(String loginId, String pwd, boolean autoLogin) {
        this.loginId = loginId;
        this.pwd = pwd;
        this.loginType = LoginType.EMAIL;
        this.loginTime = LocalDateTime.now();
        this.autoLogin = autoLogin;
    }

    // Service단 Reqeust로 변경
    public LoginServiceRequest toServiceRequest() {
        return LoginServiceRequest.builder()
                .loginId(this.loginId)
                .pwd(this.pwd)
                .loginType(this.loginType)
                .loginTime(this.loginTime)
                .autoLogin(this.autoLogin)
                .build();
    }
}
