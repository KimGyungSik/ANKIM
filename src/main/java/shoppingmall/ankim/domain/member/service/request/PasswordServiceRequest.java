package shoppingmall.ankim.domain.member.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordServiceRequest {

    private String password; // 비밀번호

    @Builder
    public PasswordServiceRequest(String password) {
        this.password = password;
    }
}
