package shoppingmall.ankim.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shoppingmall.ankim.domain.delivery.entity.Delivery;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    @Query("SELECT d FROM Delivery d JOIN FETCH d.order")
    List<Delivery> findAllWithOrder();
}
