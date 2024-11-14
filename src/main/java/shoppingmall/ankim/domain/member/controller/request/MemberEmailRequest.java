package shoppingmall.ankim.domain.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
public class MemberEmailRequest {

    @NotBlank(message = "이메일을 입력해주세요.") // null 과 "" 과 " " 모두 허용하지 않는다.
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    private String id; // 아이디(이메일)

    @Builder
    public MemberEmailRequest(String id) {
        this.id = id;
    }
}
