package shoppingmall.ankim.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.order.entity.Order;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    @Query("SELECT d FROM Delivery d JOIN FETCH d.order")
    List<Delivery> findAllWithOrder();

    @Query("SELECT d FROM Delivery d where d.order.ordNo = :orderId")
    Optional<Delivery> findByOrder(String orderId);
}
