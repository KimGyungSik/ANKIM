package shoppingmall.ankim.domain.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.member.service.request.PasswordServiceRequest;

@Data
@NoArgsConstructor
public class PasswordRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$",
            message = "비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password; // 비밀번호

    @Builder
    public PasswordRequest(String password) {
        this.password = password;
    }

    public PasswordServiceRequest toServiceRequest() {
        return PasswordServiceRequest.builder()
                .password(this.password)
                .build();
    }
}
