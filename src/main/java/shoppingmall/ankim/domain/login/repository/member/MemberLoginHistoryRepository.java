package shoppingmall.ankim.domain.login.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginHistory;
import shoppingmall.ankim.domain.login.repository.member.query.MemberLoginAttemptQueryRepository;
import shoppingmall.ankim.domain.login.repository.member.query.MemberLoginHistoryQueryRepository;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Optional;

@Repository
public interface MemberLoginHistoryRepository extends JpaRepository<MemberLoginHistory, Long>, MemberLoginHistoryQueryRepository {

}
