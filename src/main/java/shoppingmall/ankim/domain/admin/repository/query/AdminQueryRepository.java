package shoppingmall.ankim.domain.admin.repository.query;

import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.member.entity.Member;

public interface AdminQueryRepository {
    // 퇴사하지 않은 회원을 조회한다.
    Admin findByLoginIdExcludingResigned(String loginId);
}
