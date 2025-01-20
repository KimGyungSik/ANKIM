package shoppingmall.ankim.domain.login.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.login.entity.member.LoginType;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class MemberLoginRequest {

    @NotBlank(message = "이메일을 입력해주세요.") // null 과 "" 과 " " 모두 허용하지 않는다.
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "올바른 이메일을 입력해주세요."
    )
    private String loginId; // 아이디(이메일)

    @NotBlank(message = "비밀번호를 입력해주세요.")
//    @Pattern(
//            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$",
//            message = "올바른 비밀번호를 입력해주세요."
//    )
    private String password; // 비밀번호

    private LoginType loginType; // 로그인 타입 (EMAIL)
    private LocalDateTime loginTime; // 로그인 시각
    private String autoLogin; // 자동 로그인 여부(자동로그인 null, 일반 로그인 "rememberMe")

    @Builder
    public MemberLoginRequest(String loginId, String password, String autoLogin) {
        this.loginId = loginId;
        this.password = password;
        this.loginType = LoginType.EMAIL;
        this.loginTime = LocalDateTime.now();
        this.autoLogin = autoLogin;
    }

    // Service단 Reqeust로 변경
    public LoginServiceRequest toServiceRequest() {
        return LoginServiceRequest.builder()
                .loginId(this.loginId)
                .pwd(this.password)
                .loginType(this.loginType)
                .loginTime(this.loginTime)
                .autoLogin(this.autoLogin)
                .build();
    }
}
