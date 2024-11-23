package shoppingmall.ankim.domain.admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.admin.service.request.AdminIdValidServiceRequest;

@Getter
@NoArgsConstructor
public class AdminIdValidRequest {

    @NotBlank(message = "올바른 아이디를 입력해주세요.")
    @Pattern(
            regexp = "^(?!\\d)[a-z][a-z\\d]{2,15}$",
            message = "아이디는 3~16자의 소문자와 숫자만 사용하며 숫자로 시작할 수 없습니다."
    )
    private String loginId; // 아이디

    @Builder
    public AdminIdValidRequest(String loginId) {
        this.loginId = loginId;
    }

    public AdminIdValidServiceRequest toServiceRequest() {
        return AdminIdValidServiceRequest.builder()
                .loginId(this.loginId)
                .build();
    }

}
