package shoppingmall.ankim.domain.login.entity.member.loginHistory;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "login_attpt")
public class MemberLoginAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @Column(name = "fail_cnt")
    private Integer failCount; // 공통

    @Column(name = "last_attpt_time")
    private LocalDateTime lastAttemptTime = LocalDateTime.now(); // 공통

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime; // 공통

    @Column(name = "active_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String activeYn = "Y"; // 공통
}
