package shoppingmall.ankim.domain.member.repository.query;

import shoppingmall.ankim.domain.member.entity.Member;

public interface MemberQueryRepository {

    // 탈퇴하지 않은 회원을 조회한다.
    Member findByLoginIdExcludingWithdrawn(String loginId);
}
