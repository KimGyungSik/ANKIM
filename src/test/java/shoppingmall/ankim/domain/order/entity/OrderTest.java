package shoppingmall.ankim.domain.order.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.INIT;

class OrderTest {

    @DisplayName("주문 생성 시 주문 항목 리스트에서 총 주문 금액을 계산한다.")
    @Test
    void calculateTotalPrice() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getPrice()).willReturn(10000);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getPrice()).willReturn(20000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(30000); // 10000 + 20000
    }


    @DisplayName("주문 생성 시 총 배송비를 계산한다.")
    @Test
    void calculateTotalShipFee() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getShipFee()).willReturn(1000);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getShipFee()).willReturn(2000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getTotalShipFee()).isEqualTo(3000); // 1000 + 2000
    }

    @DisplayName("주문 생성 시 총 할인 금액을 계산한다.")
    @Test
    void calculateTotalDiscPrice() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getDiscPrice()).willReturn(0);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getDiscPrice()).willReturn(5000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getTotalDiscPrice()).isEqualTo(5000); // 0 + 5000
    }

    @DisplayName("주문 생성 시 최종 결제 금액을 계산한다.")
    @Test
    void calculatePayAmt() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getPrice()).willReturn(10000);
        given(orderItem1.getDiscPrice()).willReturn(0);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getPrice()).willReturn(20000);
        given(orderItem2.getDiscPrice()).willReturn(5000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getPayAmt()).isEqualTo(25000); // (30000 - 5000)
    }

    @DisplayName("주문 생성 시 주문 상태는 INIT이다.")
    @Test
    void init() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        OrderItem orderItem2 = mock(OrderItem.class);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(INIT);
    }

    @DisplayName("주문 생성 시 등록 시간을 기록한다.")
    @Test
    void registeredDateTime() {
        // given
        LocalDateTime regDate = LocalDateTime.now();

        OrderItem orderItem1 = mock(OrderItem.class);
        OrderItem orderItem2 = mock(OrderItem.class);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getRegDate()).isEqualTo(regDate);
    }
}
