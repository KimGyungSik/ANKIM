package shoppingmall.ankim.domain.login.entity.member.loginHistory;


import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "login_attpt")
public class MemberLoginAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @Embedded
    private BaseLoginAttempt loginAttemptDetails;

    @Builder
    public MemberLoginAttempt(Long no, Member member, BaseLoginAttempt loginAttemptDetails) {
        this.no = no;
        this.member = member;
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

    public void increaseFailCount() {
        this.loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(this.loginAttemptDetails.getFailCount() + 1)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(this.loginAttemptDetails.getUnlockTime())
                .activeYn(this.loginAttemptDetails.getActiveYn())
                .build();
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


    /*
    @Column(name = "fail_cnt")
    private Integer failCount; // 공통

    @Column(name = "last_attpt_time")
    private LocalDateTime lastAttemptTime = LocalDateTime.now(); // 공통

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime; // 공통

    @Column(name = "active_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String activeYn = "Y"; // 공통
*/

}
