package shoppingmall.ankim.factory;

import jakarta.persistence.EntityManager;
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

public class OrderFactory {

    public static List<Order> createOrders(EntityManager entityManager, int count) {
        // Member를 하나만 생성
        Member member = MemberJwtFactory.createMember(entityManager, "0711kyungh@naver.com");

        // Product 및 관련 데이터 생성
        Product product = ProductFactory.createProduct(entityManager);

        // Item 추출
        Item item1 = product.getItems().get(0); // 첫 번째 품목
        Item item2 = product.getItems().get(1); // 두 번째 품목

        // Order 생성
        OrderItem orderItem1 = OrderItem.create(item1, 2); // 수량 2
        OrderItem orderItem2 = OrderItem.create(item2, 3); // 수량 3

        // 여러 개의 주문 생성
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Order order = makeOrders(entityManager, member,List.of(orderItem1,orderItem2)); // 동일한 Member 사용
            orders.add(order);
        }
        return orders;
    }

    public static Order makeOrders(EntityManager entityManager, Member member, List<OrderItem> orderItems) {
        // Delivery 생성
        Delivery delivery = createDelivery(entityManager, member);

        // Order 생성
        Order order = Order.create(
                List.of(orderItems.get(0), orderItems.get(1)),
                member, // 동일한 Member 사용
                null,
                LocalDateTime.now()
        );

        entityManager.persist(order);

        return order;
    }

    public static Order createOrderWithOutDelivery(EntityManager entityManager) {
        // Product 및 관련 데이터 생성
        Product product = ProductFactory.createProduct(entityManager);
        entityManager.persist(product);

        // Item 추출
        Item item1 = product.getItems().get(2); // 첫 번째 품목
        Item item2 = product.getItems().get(3); // 두 번째 품목

        // Order 생성
        OrderItem orderItem1 = OrderItem.create(item1, 2); // 수량 2
        OrderItem orderItem2 = OrderItem.create(item2, 3); // 수량 3

        // Member 생성
        Member member = MemberJwtFactory.createMember(entityManager, "0711kyungh@naver.com");

        // Order 생성
        Order order = Order.create(
                List.of(orderItem1, orderItem2),
                member,
                null,
                LocalDateTime.now()
        );
        return order;
    }

    public static Order createOrderWithDelivery(EntityManager entityManager) {
        // Product 및 관련 데이터 생성
        Product product = ProductFactory.createProduct(entityManager);

        // Item 추출
        Item item1 = product.getItems().get(2); // 첫 번째 품목
        Item item2 = product.getItems().get(3); // 두 번째 품목

        // Order 생성
        OrderItem orderItem1 = OrderItem.create(item1, 2); // 수량 2
        OrderItem orderItem2 = OrderItem.create(item2, 3); // 수량 3

        // Member 생성
        Member member = MemberJwtFactory.createMember(entityManager, "0711kyungh@naver.com");

        // Delivery 생성
        Delivery delivery = createDelivery(entityManager, member);

        // Order 생성
        Order order = Order.create(
                List.of(orderItem1, orderItem2),
                member,
                delivery,
                LocalDateTime.now()
        );

        entityManager.persist(order);

        entityManager.flush();
        entityManager.clear();

        // Cart 생성
        Cart cart = Cart.create(member, LocalDateTime.now());

        createCartItem(entityManager, cart, member,product,item1,2);
        createCartItem(entityManager, cart, member,product,item2,3);

        entityManager.persist(cart);
        return order;
    }

    public static void createCartItem(EntityManager entityManager, Cart cart, Member member,Product product,Item item,Integer qty) {
        // CartItem 생성
        CartItem cartItem = CartItem.create(cart, product, item, qty, LocalDateTime.now());
        cart.addCartItem(cartItem);
    }

    private static Delivery createDelivery(EntityManager entityManager, Member member) {
        MemberAddress address = MemberAddress.create(
                member,
                "집",
                BaseAddress.builder()
                        .zipCode(12345)
                        .addressMain("서울시 강남구 테헤란로 123")
                        .addressDetail("1층")
                        .build(),
                "010-1234-5678",
                "010-5678-1234",
                "Y"
        );
        entityManager.persist(address);

        Delivery delivery = Delivery.create(
                address,
                "FastCourier",
                "문 앞에 놓아주세요.",
                new TrackingNumberGenerator() {
                    @Override
                    public String generate() {
                        return "TRACK123456";
                    }
                }
        );
        entityManager.persist(delivery);

        return delivery;
    }


}