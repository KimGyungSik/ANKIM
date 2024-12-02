package shoppingmall.ankim.domain.orderItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Query("SELECT o.item, o.qty FROM OrderItem o WHERE o.no IN :orderItemIds")
    List<Object[]> findItemsAndQuantitiesByOrderItemIds(@Param("orderItemIds") List<Long> orderItemIds);
}
