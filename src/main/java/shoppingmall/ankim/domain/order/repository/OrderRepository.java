package shoppingmall.ankim.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.order.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.member " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.item i " +
            "WHERE o.ordCode = :orderName " +
            "ORDER BY i.no ASC")
    Optional<Order> findByOrderNameWithMemberAndOrderItemsAndItem(@Param("orderName") String orderName);


    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.member " +
            "JOIN FETCH o.orderItems oi " +
            "WHERE o.ordCode = :orderName")
    Optional<Order> findByOrderNameWithMemberAndOrderItems(@Param("orderName") String orderName);


    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.member JOIN FETCH o.orderItems WHERE o.ordNo = :orderId")
    Optional<Order> findByOrderIdWithMemberAndOrderItems(@Param("orderId") String orderId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.member " +
            "JOIN FETCH o.delivery " +
            "JOIN FETCH o.orderItems oi " +
            "WHERE o.ordNo = :orderId")
    Optional<Order> findByOrderIdWithMemberAndDeliveryAndOrderItems(@Param("orderId") String orderId);

    Optional<Order> findByOrdNo(String ordNo);

    @Query("SELECT o FROM Order o WHERE o.ordCode = :orderName")
    Optional<Order> findByOrdName(@Param("orderName") String orderName);
    List<Order> findByOrdNoIn(@Param("ordNo") List<String> ordNo);

    boolean existsByOrdCode(String ordCode); // 주문번호 확인
    @Query("SELECT o.ordCode FROM Order o")
    List<String> findAllByOrdCode();
}
