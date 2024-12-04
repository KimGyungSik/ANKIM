package shoppingmall.ankim.domain.memberHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.memberHistory.entity.MemberHistory;

public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
}
