package shoppingmall.ankim.domain.login.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.login.entity.member.LoginType;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseLoginHistory {
    @Column(name = "ip_addr", length = 45, nullable = false)
    private String ipAddress;

    @Column(name = "login_att_dt", nullable = false)
    private LocalDateTime loginAttemptDate = LocalDateTime.now();

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y";

    public BaseLoginHistory(LocalDateTime loginAttemptDate, String ipAddress, String activeYn) {
        this.loginAttemptDate = loginAttemptDate;
        this.ipAddress = ipAddress;
        this.activeYn = activeYn;
    }
}
