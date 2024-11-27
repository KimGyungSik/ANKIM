package shoppingmall.ankim.factory;

import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberJwtFactory {
    public static Member createMember(String loginId) {
        return Member.builder()
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
    }

    public static String createAccessToken(Member member, JwtTokenProvider jwtTokenProvider) {
        CustomUserDetails userDetails = new CustomUserDetails(member);
        return jwtTokenProvider.generateAccessToken(userDetails, "access");
    }
}
