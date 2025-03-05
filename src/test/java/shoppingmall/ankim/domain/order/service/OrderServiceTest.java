package shoppingmall.ankim.domain.order.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartItemNotFoundException;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.dto.OrderTempResponse;
import shoppingmall.ankim.domain.order.exception.OrderCodeGenerationException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.factory.CartFactory;
import shoppingmall.ankim.factory.MemberFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private OrderService orderService;

    @SpyBean // 실제 빈을 사용하면서 특정 메서드에서는 mock객체를 사용하기 위해서 사용
    private OrderRepository orderRepository;

    @MockBean
    private S3Service s3Service;

    @DisplayName("장바구니에서 품목을 선택하여 주문할 수 있다.")
    @Test
    public void createOrder_selectOrderTempItems() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberFactory.createMember(em, loginId);

        List<CartItem> cartItemList = CartFactory.createCart(em, member);
        Long cartItemNo1 = cartItemList.get(0).getNo();
        Long cartItemNo2 = cartItemList.get(1).getNo();

        List<Long> cartItemNoList = List.of(cartItemNo1, cartItemNo2);

        // when
        OrderTempResponse tempOrder = orderService.createOrderTemp(loginId, cartItemNoList);

        // then
        assertNotNull(tempOrder);
        assertEquals(cartItemNoList.size(), tempOrder.getItems().size());
        assertTrue(tempOrder.getTotalQty() > 0);
        assertNotNull(tempOrder.getOrderCode());
    }

    @DisplayName("장바구니에서 전체 품목을 선택하여 주문할 수 있다.")
    @Test
    public void createOrder_selectAllOrderTempItems() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberFactory.createMember(em, loginId);

        List<CartItem> cartItemList = CartFactory.createCart(em, member);
        List<Long> cartItemNoList = new ArrayList<>();
        int totalQty = 0;
        for (int i = 0; i < cartItemList.size(); i++) {
            cartItemNoList.add(cartItemList.get(i).getNo());
            totalQty += cartItemList.get(i).getQty();
        }

        // when
        OrderTempResponse tempOrder = orderService.createOrderTemp(loginId, cartItemNoList);

        // then
        assertNotNull(tempOrder);
        assertEquals(cartItemNoList.size(), tempOrder.getItems().size());
        assertEquals(totalQty, (int) tempOrder.getTotalQty());
        assertNotNull(tempOrder.getOrderCode());
    }

    @DisplayName("장바구니에서 품목을 선택하지않고 주문하면 NO_SELECTED_CART_ITEM예외가 발생한다.")
    @Test
    public void createOrder_Temp_withoutSelectingItems_throwsException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberFactory.createMember(em, loginId);

        List<CartItem> cartItemList = CartFactory.createCart(em, member);
        List<Long> cartItemNoList = new ArrayList<>();

        // when & then
        Assertions.assertThatThrownBy(() -> orderService.createOrderTemp(loginId, cartItemNoList))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessageContaining(NO_SELECTED_CART_ITEM.getMessage());
    }

    @DisplayName("장바구니에 없는 품목을 선택하면 CART_ITEM_NOT_FOUND 예외가 발생한다.")
    @Test
    public void createOrder_Temp_withInvalidCartItem_throwsException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberFactory.createMember(em, loginId);

        List<CartItem> cartItemList = CartFactory.createCart(em, member);
        Long invalidCartItemNo = -1L; // 유효하지 않은 ID
        List<Long> cartItemNoList = List.of(invalidCartItemNo);

        // when & then
        Assertions.assertThatThrownBy(() -> orderService.createOrderTemp(loginId, cartItemNoList))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessageContaining(CART_ITEM_NOT_FOUND.getMessage());
    }

    @DisplayName("주문 코드 생성 중 에러가 발생하면 ORDER_CODE_GENERATE_FAIL 예외가 발생한다.")
    @Test
    public void createOrder_withOrderTempCodeGenerationError_throwsException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberFactory.createMember(em, loginId);

        List<CartItem> cartItemList = CartFactory.createCart(em, member);
        List<Long> cartItemNoList = List.of(cartItemList.get(0).getNo());

        // 임의로 주문 코드 생성에 실패하도록 예외를 발생시킴
        Mockito.doThrow(new OrderCodeGenerationException(ORDER_CODE_GENERATE_FAIL))
                .when(orderRepository).existsByOrdCode(any());

        // when & then
        Assertions.assertThatThrownBy(() -> orderService.createOrderTemp(loginId, cartItemNoList))
                .isInstanceOf(OrderCodeGenerationException.class)
                .hasMessageContaining(ORDER_CODE_GENERATE_FAIL.getMessage());
    }
}