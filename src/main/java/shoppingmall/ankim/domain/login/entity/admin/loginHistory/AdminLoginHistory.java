package shoppingmall.ankim.domain.login.entity.admin.loginHistory;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.login.entity.BaseLoginHistory;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginHistory;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
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

    @Embedded
    private BaseLoginHistory loginHistory;

    @Builder
    public AdminLoginHistory(Long no, Admin admin, BaseLoginHistory loginHistory) {
        this.no = no;
        this.admin = admin;
        this.loginHistory = loginHistory;
    }

    public static AdminLoginHistory recordLoginHistory(Admin admin, String ipAddress, LoginServiceRequest loginServiceRequest) {
        // BaseLoginHistory 객체 생성
        BaseLoginHistory baseLoginHistory = BaseLoginHistory.builder()
                .ipAddress(ipAddress)
                .loginAttemptDate(loginServiceRequest.getLoginTime())
                .activeYn("Y")
                .build();

        // MemberLoginHistory 객체 생성
        AdminLoginHistory adminLoginHistory = AdminLoginHistory.builder()
                .admin(admin)
                .loginHistory(baseLoginHistory)
                .build();

        return adminLoginHistory;
    }
}