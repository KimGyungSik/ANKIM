package shoppingmall.ankim.domain.login.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.login.entity.admin.loginHistory.AdminLoginHistory;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginHistory;
import shoppingmall.ankim.domain.login.repository.admin.query.AdminLoginHistoryQueryRepository;
import shoppingmall.ankim.domain.login.repository.member.query.MemberLoginHistoryQueryRepository;

@Repository
public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long>, AdminLoginHistoryQueryRepository {

}
