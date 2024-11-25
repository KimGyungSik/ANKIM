package shoppingmall.ankim.domain.login.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.login.entity.admin.loginHistory.AdminLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.repository.admin.query.AdminLoginAttemptQueryRepository;
import shoppingmall.ankim.domain.login.repository.member.query.MemberLoginAttemptQueryRepository;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Optional;

@Repository
public interface AdminLoginAttemptRepository extends JpaRepository<AdminLoginAttempt, Long>, AdminLoginAttemptQueryRepository {

    Optional<AdminLoginAttempt> findByAdminAndLoginAttemptDetailsActiveYn(Admin admin, String activeYn);
}
