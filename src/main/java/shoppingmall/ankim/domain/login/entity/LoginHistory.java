package shoppingmall.ankim.domain.login.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 기본 키

    @ManyToOne
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호 (FK)

    @Column(name = "ip_addr", length = 45, nullable = false)
    private String ipAddr; // 접속 IP

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType = LoginType.EMAIL; // 로그인 타입 (EMAIL, NAVER, KAKAO 등)

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate; // 로그인 시도 시간

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y";

}