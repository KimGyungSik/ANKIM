package shoppingmall.ankim.domain.login.entity.member.loginHistory;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.BaseLoginHistory;
import shoppingmall.ankim.domain.login.entity.member.LoginType;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "login_history")
public class MemberLoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호 (FK)

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType = LoginType.EMAIL; // 로그인 타입 (EMAIL, NAVER, KAKAO 등)

    @Embedded
    private BaseLoginHistory loginHistory;

    @Builder
    public MemberLoginHistory(Long no, Member member, LoginType loginType, BaseLoginHistory loginHistory) {
        this.no = no;
        this.member = member;
        this.loginType = loginType;
        this.loginHistory = loginHistory;
    }

    public static MemberLoginHistory recordLoginHistory(Member member, String ipAddress, LoginServiceRequest loginServiceRequest) {
        // BaseLoginHistory 객체 생성
        BaseLoginHistory baseLoginHistory = BaseLoginHistory.builder()
                .ipAddress(ipAddress)
                .loginAttemptDate(loginServiceRequest.getLoginTime())
                .activeYn("Y")
                .build();

        // MemberLoginHistory 객체 생성
        MemberLoginHistory memberLoginHistory = MemberLoginHistory.builder()
                .member(member)
                .loginType(loginServiceRequest.getLoginType())
                .loginHistory(baseLoginHistory)
                .build();

        return memberLoginHistory;
    }
}