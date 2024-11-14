package shoppingmall.ankim.domain.login.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일을 입력해주세요.") // null 과 "" 과 " " 모두 허용하지 않는다.
    private String id; // 아이디(이메일)

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String pwd; // 비밀번호

    @Builder
    public LoginRequest(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }
}
