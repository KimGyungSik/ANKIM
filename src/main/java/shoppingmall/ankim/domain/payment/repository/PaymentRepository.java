package shoppingmall.ankim.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where p.order.ordNo = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") String orderId);

    @Query("select p from Payment p JOIN FETCH p.order where p.payKey = :payKey")
    Optional<Payment> findByPayKeyWithOrder(@Param("payKey") String payKey);

    List<Payment> findByOrderIn(@Param("orders") List<Order> orders);
}
