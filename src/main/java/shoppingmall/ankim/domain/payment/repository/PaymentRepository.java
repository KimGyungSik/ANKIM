package shoppingmall.ankim.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.payment.entity.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where p.order.ordNo = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") String orderId);

    @Query("select p from Payment p where p.payKey = :paymentKey")
    Optional<Payment> findByPayKey(String paymentKey);
}
