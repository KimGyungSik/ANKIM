package shoppingmall.ankim.domain.security.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityContextHelper {
    public String getLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            throw new IllegalStateException("인증 정보가 존재하지 않습니다.");
        }
        return authentication.getName();
    }
}
