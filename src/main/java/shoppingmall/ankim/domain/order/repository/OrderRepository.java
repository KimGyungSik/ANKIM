package shoppingmall.ankim.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
