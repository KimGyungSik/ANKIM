package shoppingmall.ankim.factory;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CartFactory {

    public static List<CartItem> createCart(EntityManager entityManager, Member member) {
        // Product 및 관련 데이터 생성
        Product product = ProductFactory.createProduct(entityManager);

        // Item 추출
        Item item1 = product.getItems().get(0); // 첫 번째 품목
        Item item2 = product.getItems().get(1); // 두 번째 품목

        // Cart 생성
        LocalDateTime registerDate = LocalDateTime.now();
        Cart cart = Cart.create(member, registerDate);

        entityManager.persist(cart);

        // Product 정보 추출
        Product product1 = item1.getProduct();
        Product product2 = item2.getProduct();
//        Product product3 = item3.getProduct();

        CartItem cartItem1 = CartItem.create(cart, product1, item1, 2, registerDate);
        CartItem cartItem2 = CartItem.create(cart, product2, item2, 3, registerDate);
//        CartItem cartItem3 = CartItem.create(cart, product3, item3, 1, registerDate);

        entityManager.persist(cartItem1);
        entityManager.persist(cartItem2);
//        entityManager.persist(cartItem3);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);

        return cartItems;
    }
}
