package shoppingmall.ankim.domain.login.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseLoginAttempt {
    @Column(name = "fail_cnt")
    private Integer failCount;

    @Column(name = "last_attpt_time")
    private LocalDateTime lastAttemptTime = LocalDateTime.now();

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "active_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String activeYn = "Y";

    public BaseLoginAttempt(Integer failCount, LocalDateTime lastAttemptTime, LocalDateTime unlockTime, String activeYn) {
        this.failCount = failCount;
        this.lastAttemptTime = lastAttemptTime;
        this.unlockTime = unlockTime;
        this.activeYn = activeYn;
    }
}
