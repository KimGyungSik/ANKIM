package shoppingmall.ankim.domain.cart.repository.query;

import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;

public interface CartItemQueryRepository {
    List<CartItem> findOutOfStockItems(Member member);
    Integer countActiveCartItems(Member member);

}
