package shoppingmall.ankim.domain.login.entity.member.loginHistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.login.entity.member.LoginType;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "login_history")
public class MemberLoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호 (FK)

    @Column(name = "ip_addr", length = 45, nullable = false)
    private String ipAddress; // 공통

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType = LoginType.EMAIL; // 로그인 타입 (EMAIL, NAVER, KAKAO 등)

    @Column(name = "login_att_dt", nullable = false)
    private LocalDateTime loginAttemptDate = LocalDateTime.now(); // 공통

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y"; // 공통

}