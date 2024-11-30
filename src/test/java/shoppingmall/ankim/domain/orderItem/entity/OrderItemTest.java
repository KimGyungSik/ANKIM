package shoppingmall.ankim.domain.orderItem.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.orderItem.exception.DiscountPriceException;
import shoppingmall.ankim.domain.orderItem.exception.InvalidOrderItemQtyException;
import shoppingmall.ankim.domain.product.entity.Product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class OrderItemTest {
    @Test
    @DisplayName("정상적인 주문 수량으로 OrderItem 생성할 수 있다.")
    void createOrderItem_withValidQty() {
        // given
        Product product = mock(Product.class); // Mock Product 객체 생성
        given(product.getSellPrice()).willReturn(8000); // 할인 적용 가격 설정
        given(product.getShipFee()).willReturn(3000); // 배송비 설정

        Item item = mock(Item.class); // Mock Item 객체 생성
        given(item.getTotalPrice()).willReturn(10000); // 정상 가격 설정
        given(item.getProduct()).willReturn(product); // Mock Product 객체를 반환하도록 설정

        // when
        OrderItem orderItem = OrderItem.create(item, 2);

        // then
        assertThat(orderItem.getQty()).isEqualTo(2);
    }

    @Test
    @DisplayName("주문 수량이 0일 경우 예외가 발생한다.")
    void createOrderItem_withZeroQty() {
        // given
        Item item = mock(Item.class);

        // then
        assertThrows(InvalidOrderItemQtyException.class, () -> OrderItem.create(item, 0));
    }

    @Test
    @DisplayName("주문 수량이 null일 경우 예외가 발생한다.")
    void createOrderItem_withNullQty() {
        // given
        Item item = mock(Item.class);

        // then
        assertThrows(InvalidOrderItemQtyException.class, () -> OrderItem.create(item, null));
    }

    @Test
    @DisplayName("정상적인 할인 금액 계산이 가능하다.")
    void createOrderItem_withValidDiscount() {
        // given
        Product product = mock(Product.class); // Mock Product 객체 생성
        given(product.getSellPrice()).willReturn(8000); // 할인 적용 가격 설정
        given(product.getShipFee()).willReturn(3000); // 배송비 설정

        Item item = mock(Item.class); // Mock Item 객체 생성
        given(item.getTotalPrice()).willReturn(10000); // 정상 가격 설정
        given(item.getProduct()).willReturn(product); // Mock Product 객체를 반환하도록 설정

        // when
        OrderItem orderItem = OrderItem.create(item, 2);

        // then
        assertThat(orderItem.getDiscPrice()).isEqualTo(2000); // 할인 금액 검증
        assertThat(orderItem.getQty()).isEqualTo(2); // 수량 검증
    }


    @Test
    @DisplayName("할인 금액이 음수일 경우 예외가 발생한다.")
    void createOrderItem_withInvalidDiscount() {
        // given
        Product product = mock(Product.class); // Mock Product 객체 생성
        given(product.getSellPrice()).willReturn(8000); // 할인 적용 가격 설정
        given(product.getShipFee()).willReturn(3000); // 배송비 설정

        Item item = mock(Item.class); // Mock Item 객체 생성
        given(item.getTotalPrice()).willReturn(5000); // 정상 가격 설정
        given(item.getProduct()).willReturn(product); // Mock Product 객체를 반환하도록 설정

        // then
        assertThrows(DiscountPriceException.class, () -> OrderItem.create(item, 2));
    }

}