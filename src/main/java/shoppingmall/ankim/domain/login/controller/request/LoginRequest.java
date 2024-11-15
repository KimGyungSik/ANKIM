package shoppingmall.ankim.domain.login.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일을 입력해주세요.") // null 과 "" 과 " " 모두 허용하지 않는다.
    private String loginId; // 아이디(이메일)

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String pwd; // 비밀번호

    public LoginRequest(String loginId, String pwd) {
        this.loginId = loginId;
        this.pwd = pwd;
    }

    //    private String loginType; // 로그인 타입 (EMAIL)
//    private LocalDateTime loginTime; // 로그인 시각
//
//    @Builder
//    public LoginRequest(String id, String pwd, String loginType) {
//        this.id = id;
//        this.pwd = pwd;
//        this.loginType = loginType;
//        this.loginTime = LocalDateTime.now();
//    }
}
