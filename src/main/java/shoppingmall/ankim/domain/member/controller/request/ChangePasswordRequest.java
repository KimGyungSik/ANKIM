package shoppingmall.ankim.domain.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;

@Getter
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    private String oldPassword; // 기존 비밀번호

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$",
            message = "새로운 비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword; // 새로운 비밀번호

    @NotBlank
    private String confirmPassword; // 새로운 비밀번호 일치 확인

    @Builder
    public ChangePasswordRequest(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Service단 Reqeust로 변경
    public ChangePasswordServiceRequest toServiceRequest() {
        return ChangePasswordServiceRequest.builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .confirmPassword(confirmPassword)
                .build();
    }
}
