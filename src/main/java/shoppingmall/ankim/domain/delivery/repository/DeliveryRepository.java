package shoppingmall.ankim.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
}
