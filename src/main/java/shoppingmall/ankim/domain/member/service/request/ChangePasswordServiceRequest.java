package shoppingmall.ankim.domain.member.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordServiceRequest {

    private String oldPassword; // 기존 비밀번호
    private String newPassword; // 새로운 비밀번호
    private String confirmPassword; // 새로운 비밀번호 일치 확인

    @Builder
    public ChangePasswordServiceRequest(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
