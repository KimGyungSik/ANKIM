package shoppingmall.ankim.domain.member.repository.query;

import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Optional;

public interface MemberQueryRepository {
    // 탈퇴하지 않은 회원을 조회한다.
    Optional<Member> findByLoginIdExcludingWithdrawn(String loginId);
//    Member findByLoginIdExcludingWithdrawn(String loginId);
}
