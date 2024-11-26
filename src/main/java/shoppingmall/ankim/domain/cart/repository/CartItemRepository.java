package shoppingmall.ankim.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.repository.query.CartItemQueryRepository;
import shoppingmall.ankim.domain.cart.repository.query.CartQueryRepository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemQueryRepository {

    // 특정 장바구니에 담은 품목이 존재하는지 확인한다.
    Optional<CartItem> findByCartAndItemNo(Cart cart, Item item);

}