package shoppingmall.ankim.domain.login.entity.admin.loginHistory;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.admin.entity.Admin;
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

    @Column(name = "fail_cnt")
    private Integer failCount;

    @Column(name = "last_attpt_time")
    private LocalDateTime lastAttemptTime = LocalDateTime.now();

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "active_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String activeYn = "Y";
}
