package shoppingmall.ankim.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.repository.query.AdminQueryRepository;
import shoppingmall.ankim.domain.member.entity.Member;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminQueryRepository {
    // 이메일 중복 검사를 한다.
    Boolean existsByLoginId(String loginId);

    // loginId를 조회한다.
    Admin findByLoginId(String loginId);
}
