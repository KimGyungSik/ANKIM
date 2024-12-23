package shoppingmall.ankim.domain.leaveReason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Optional;

public interface LeaveReasonRepository extends JpaRepository<LeaveReason, Long> {

    Optional<LeaveReason> findByNo(Long No);
}
