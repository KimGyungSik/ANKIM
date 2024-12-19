package shoppingmall.ankim.domain.login.entity.admin.loginHistory;


import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin_login_attpt")
public class AdminLoginAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no", nullable = false)
    private Admin admin;

    @Embedded
    private BaseLoginAttempt loginAttemptDetails;

    @Builder
    public AdminLoginAttempt(Long no, Admin admin, BaseLoginAttempt loginAttemptDetails) {
        this.no = no;
        this.admin = admin;
        this.loginAttemptDetails = loginAttemptDetails;
    }

    public void deactivateLoginAttempt() {
        this.loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(this.loginAttemptDetails.getFailCount())
                .lastAttemptTime(this.loginAttemptDetails.getLastAttemptTime())
                .unlockTime(this.loginAttemptDetails.getUnlockTime())
                .activeYn("N") // 비활성화
                .build();
    }

    public int increaseFailCount() {
        int failCount = this.loginAttemptDetails.getFailCount() + 1;
        this.loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(failCount)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(this.loginAttemptDetails.getUnlockTime())
                .activeYn(this.loginAttemptDetails.getActiveYn())
                .build();
        return failCount;
    }

    public void setLockTime(int lockMinutes) {
        this.loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(this.loginAttemptDetails.getFailCount())
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(LocalDateTime.now().plusMinutes(lockMinutes))
                .activeYn(this.loginAttemptDetails.getActiveYn())
                .build();
    }

    public boolean isUnlockTimePassed() {
        return this.loginAttemptDetails.getUnlockTime() != null &&
                this.loginAttemptDetails.getUnlockTime().isBefore(LocalDateTime.now());
    }

    public void resetFailCount() {
        this.loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(this.loginAttemptDetails.getFailCount())
                .lastAttemptTime(LocalDateTime.now()) // 마지막 시도 시간 갱신
                .unlockTime(this.loginAttemptDetails.getUnlockTime()) // 잠금 시간 초기화
                .activeYn("N") // 활성화 상태 비활성으로 업데이트
                .build();
    }
}
