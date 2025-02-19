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
import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.PENDING_PAYMENT;

class OrderTest {

    @DisplayName("주문 생성 시 주문 항목 리스트에서 총 주문 금액을 계산한다.")
    @Test
    void calculateTotalPrice() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getPrice()).willReturn(10000);
        given(orderItem1.getQty()).willReturn(2);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getPrice()).willReturn(20000);
        given(orderItem2.getQty()).willReturn(3);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(80000); // (10000 * 2) + (20000 * 3)
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
        given(orderItem1.getDiscPrice()).willReturn(1000);
        given(orderItem1.getQty()).willReturn(2);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getDiscPrice()).willReturn(2000);
        given(orderItem2.getQty()).willReturn(3);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getTotalDiscPrice()).isEqualTo(8000); // (1000 * 2) + (2000 * 3)
    }

    @DisplayName("주문 생성 시 최종 결제 금액을 계산한다.")
    @Test
    void calculatePayAmt() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getPrice()).willReturn(10000);
        given(orderItem1.getQty()).willReturn(2);
        given(orderItem1.getDiscPrice()).willReturn(0);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getPrice()).willReturn(20000);
        given(orderItem2.getQty()).willReturn(3);
        given(orderItem2.getDiscPrice()).willReturn(5000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        assertThat(order.getPayAmt()).isEqualTo(65000); // (10000 * 2) + (20000 * 3 - 5000 * 3)
    }

    @DisplayName("주문 생성 시 주문 상태는 PENDING_PAYMENT 상태이다.")
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
        assertThat(order.getOrderStatus()).isEqualTo(PENDING_PAYMENT);
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

    @DisplayName("최종 결제 금액이 30,000원 이상이면 배송비가 0원이 된다.")
    @Test
    void freeShippingForTotalPaymentAboveThreshold() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getPrice()).willReturn(15000);
        given(orderItem1.getQty()).willReturn(2);  // 15000 * 2 = 30000
        given(orderItem1.getDiscPrice()).willReturn(0);
        given(orderItem1.getShipFee()).willReturn(2500);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getPrice()).willReturn(5000);
        given(orderItem2.getQty()).willReturn(1);
        given(orderItem2.getDiscPrice()).willReturn(2000);
        given(orderItem2.getShipFee()).willReturn(3000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        int expectedTotalPrice = (15000 * 2) + (5000 * 1);  // 30000 + 5000 = 35000
        int expectedTotalDiscPrice = (0 * 2) + (2000 * 1); // 0 + 2000 = 2000
        int expectedFinalPayment = expectedTotalPrice - expectedTotalDiscPrice; // 35000 - 2000 = 33000

        assertThat(order.getTotalPrice()).isEqualTo(expectedTotalPrice);
        assertThat(order.getTotalDiscPrice()).isEqualTo(expectedTotalDiscPrice);
        assertThat(expectedFinalPayment).isGreaterThanOrEqualTo(30000); // 30,000원 이상이어야 함
        assertThat(order.getTotalShipFee()).isEqualTo(0); // 무료배송 적용
    }

    @DisplayName("최종 결제 금액이 30,000원 미만이면 배송비가 적용된다.")
    @Test
    void shippingFeeAppliesWhenTotalPaymentBelowThreshold() {
        // given
        OrderItem orderItem1 = mock(OrderItem.class);
        given(orderItem1.getPrice()).willReturn(10000);
        given(orderItem1.getQty()).willReturn(2);  // 10000 * 2 = 20000
        given(orderItem1.getDiscPrice()).willReturn(0);
        given(orderItem1.getShipFee()).willReturn(2500);

        OrderItem orderItem2 = mock(OrderItem.class);
        given(orderItem2.getPrice()).willReturn(5000);
        given(orderItem2.getQty()).willReturn(1);
        given(orderItem2.getDiscPrice()).willReturn(2000);
        given(orderItem2.getShipFee()).willReturn(3000);

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Member member = mock(Member.class);
        Delivery delivery = mock(Delivery.class);
        LocalDateTime regDate = LocalDateTime.now();

        // when
        Order order = Order.create(orderItems, member, delivery, regDate);

        // then
        int expectedTotalPrice = (10000 * 2) + (5000 * 1);  // 20000 + 5000 = 25000
        int expectedTotalDiscPrice = (0 * 2) + (2000 * 1); // 0 + 2000 = 2000
        int expectedFinalPayment = expectedTotalPrice - expectedTotalDiscPrice; // 25000 - 2000 = 23000
        int expectedTotalShipFee = 2500 + 3000; // 배송비 총 5500원

        assertThat(order.getTotalPrice()).isEqualTo(expectedTotalPrice);
        assertThat(order.getTotalDiscPrice()).isEqualTo(expectedTotalDiscPrice);
        assertThat(expectedFinalPayment).isLessThan(30000); // 30,000원 미만이어야 함
        assertThat(order.getTotalShipFee()).isEqualTo(expectedTotalShipFee); // 무료배송 X
    }
}
