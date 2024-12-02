package shoppingmall.ankim.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.order.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // orderCode로 member를 fetch join해서 Order 조회하는 쿼리
    @Query("SELECT o FROM Order o JOIN FETCH o.member WHERE o.ordCode = :orderName")
    Optional<Order> findByOrderWithMember(@Param("orderName") String orderName);

    List<Order> findByOrdNoIn(@Param("ordNo") List<String> ordNo);

    boolean existsByOrdCode(String ordCode); // 주문번호 확인
}
