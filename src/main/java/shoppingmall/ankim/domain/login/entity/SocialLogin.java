package shoppingmall.ankim.domain.login.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "social_login")
public class SocialLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 기본 키

    @ManyToOne
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호 (FK)

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType; // 로그인 타입 (EMAIL, NAVER, KAKAO 등)

    @Column(name = "token", length = 255, nullable = false)
    private String token; // 로그인 토큰

    @Column(name = "email", length = 50)
    private String email; // 소셜 이메일

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y";

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate; // 등록일

    @Column(name = "mod_date")
    private LocalDateTime modDate; // 수정일

}
