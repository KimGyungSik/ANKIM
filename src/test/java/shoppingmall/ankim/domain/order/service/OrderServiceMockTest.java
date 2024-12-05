package shoppingmall.ankim.domain.order.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.repository.CartItemRepository;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.CartFactory;
import shoppingmall.ankim.factory.MemberFactory;
import shoppingmall.ankim.factory.MemberJwtFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
@SpringBootTest
@Transactional
class OrderServiceMockTest {

    @Autowired
    EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private CartItemRepository cartItemRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    @DisplayName("임시 주문 엔티티에 장바구니에서 선택한 상품이 성공적으로 담긴다.")
    void addToCart_ValidAccessToken_Success() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberFactory.createMember(em, loginId);

        // Mock Product
        Product mockProduct = Mockito.mock(Product.class);
        given(mockProduct.getNo()).willReturn(1L);
        given(mockProduct.getName()).willReturn("테스트 상품");
        given(productRepository.findById(1L)).willReturn(Optional.of(mockProduct));

        // Mock Item
        Item mockItem1 = Mockito.mock(Item.class);
        Item mockItem2 = Mockito.mock(Item.class);
        given(mockItem1.getNo()).willReturn(1L);
        given(mockItem1.getProduct()).willReturn(mockProduct);
        given(mockItem1.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem1.getQty()).willReturn(100);
        given(mockItem2.getNo()).willReturn(2L);
        given(mockItem2.getProduct()).willReturn(mockProduct);
        given(mockItem2.getName()).willReturn("색상: 화이트, 사이즈: M");
        given(mockItem2.getQty()).willReturn(50);

        // Mock CartItems
        CartItem cartItem1 = Mockito.mock(CartItem.class);
        CartItem cartItem2 = Mockito.mock(CartItem.class);
        given(cartItem1.getItem()).willReturn(mockItem1);
        given(cartItem1.getQty()).willReturn(2);
        given(cartItem2.getItem()).willReturn(mockItem2);
        given(cartItem2.getQty()).willReturn(1);

        List<Long> cartItemNoList = List.of(1L, 2L);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);
        given(cartItemRepository.findByNoIn(cartItemNoList)).willReturn(cartItems);

        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setOrdNo(UUID.randomUUID().toString()); // 임의의 UUID 설정
            return savedOrder;
        });

        // when
        OrderResponse orderResponse = orderService.createTempOrder(loginId, cartItemNoList);

        System.out.println("OrderResponse items size: " + orderResponse.getItems().size());
        orderResponse.getItems().forEach(item -> System.out.println("Item: " + item.getName()));

        // then
        assertNotNull(orderResponse);
        assertEquals(2, orderResponse.getItems().size()); // 예상 크기
        assertEquals("색상: 블랙, 사이즈: L", orderResponse.getItems().get(0).getName());
        assertEquals("색상: 화이트, 사이즈: M", orderResponse.getItems().get(1).getName());
    }


    @DisplayName("UUID를 숫자형식으로 변경하고 주문번호를 생성한다.")
    @Test
    public void UUIDToNumericPartialTest() throws Exception {
        // given
        String orderId = UUID.randomUUID().toString();
        LocalDateTime registeredDateTime = LocalDateTime.now();

        // 현재 날짜 (yyyyMMdd)
        String currentDate = registeredDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // UUID에서 '-' 제거
        String compactUUID = orderId.replaceAll("-", "");

        // SHA-256 해싱
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(compactUUID.getBytes());

        // 해시값 -> BigInteger 변환
        BigInteger bigInt = new BigInteger(1, hash);

        // BigInteger값을 7자리로 변환하기 위해서 10^targetLength로 나머지 연산
        BigInteger divisor = BigInteger.TEN.pow(7); // 10^7
        BigInteger compressedValue = bigInt.mod(divisor);
        String serialNumber = String.format("%07d", compressedValue); // 7자리 포맷

        // 최종 코드 생성
        String code = "ORD" + currentDate + "-" + serialNumber;
        Assertions.assertThat(code.length()).isEqualTo(19);
    }
}