package shoppingmall.ankim.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.repository.query.CartItemQueryRepository;
import shoppingmall.ankim.domain.cart.repository.query.CartQueryRepository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemQueryRepository {

    Optional<CartItem> findByCartAndItemNo(Cart cart, Item item); // 특정 장바구니에 담은 품목이 존재하는지 확인한다.
    Optional<CartItem> findByNoAndCart_Member(Long cartItemNo, Member member); // 로그인한 회원의 장바구니 품목을 조회한다.
    List<CartItem> findByNoIn( List<Long> noList);

}