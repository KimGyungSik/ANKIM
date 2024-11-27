package shoppingmall.ankim.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
