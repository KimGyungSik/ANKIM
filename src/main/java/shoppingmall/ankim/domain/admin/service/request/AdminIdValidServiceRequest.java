package shoppingmall.ankim.domain.admin.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminIdValidServiceRequest {

    private String loginId; // 아이디

    @Builder
    public AdminIdValidServiceRequest(String loginId) {
        this.loginId = loginId;
    }

}
