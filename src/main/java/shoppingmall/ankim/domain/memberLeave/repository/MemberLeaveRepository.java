package shoppingmall.ankim.domain.memberLeave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.memberLeave.entity.MemberLeave;

public interface MemberLeaveRepository extends JpaRepository<MemberLeave, Long> {
}
