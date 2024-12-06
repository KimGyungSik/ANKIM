package shoppingmall.ankim.factory;

import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberJwtFactory {

    public static Member createSecureMember(EntityManager entityManager, String loginId, String rawPwd, BCryptPasswordEncoder encoder) {
        String encodedPwd = encoder.encode(rawPwd);
        Member member = Member.builder()
                .loginId(loginId)
                .pwd(encodedPwd) // 암호화된 비밀번호 저장
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        entityManager.persist(member);
        return member;
    }

    public static Member createMember(EntityManager entityManager, String loginId) {
        Member member = Member.builder()
                .loginId(loginId)
                .pwd("password")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        entityManager.persist(member);
        return member;
    }

    public static String createToken(Member member, JwtTokenProvider jwtTokenProvider) {
        CustomUserDetails userDetails = new CustomUserDetails(member);
        return jwtTokenProvider.generateAccessToken(userDetails, "access");
    }
}