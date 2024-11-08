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
@Table(name = "admin_login_history")
public class AdminLoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no", nullable = false)
    private Admin admin;

    @Column(name = "ip_addr", length = 45, nullable = false)
    private String ipAddress; // 접속 IP

    @Column(name = "login_att_dt", nullable = false)
    private LocalDateTime loginAttemptDate = LocalDateTime.now(); // 로그인 시도 시간

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y";

}