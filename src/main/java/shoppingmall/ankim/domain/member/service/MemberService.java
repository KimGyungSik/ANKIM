package shoppingmall.ankim.domain.member.service;

public interface MemberService {
    // 아이디(이메일) 중복 검증
    Boolean emailCheck(String id);
}
