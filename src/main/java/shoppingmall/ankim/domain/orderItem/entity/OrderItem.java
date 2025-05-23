package shoppingmall.ankim.domain.orderItem.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.exception.DiscountPriceException;
import shoppingmall.ankim.domain.orderItem.exception.InvalidOrderItemQtyException;
import shoppingmall.ankim.global.audit.BaseEntity;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.time.LocalDateTime;

import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.*;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders_item")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(name = "prod_name")
    private String productName;

    @Column(name = "prod_path")
    private String thumbNailImgUrl;

    @Setter
    private Integer qty; // 주문 수량

    @Column(name = "discount_rate")
    private Integer discRate; // 할인율

    private Integer origPrice; // 주문 상품 원가

    private Integer price; // 주문 상품 가격, 정상가격(원가) + 추가금액 <- item.totalPrice

    private Integer discPrice; // 주문 할인 가격 = 정상가격(원가) - sellPrice (할인 적용된 금액)

    private Integer sellPrice; // 판매가격 ( price - discPrice )

    private Integer shipFee; // 배송비


    @Builder
    private OrderItem(Item item, String productName, String thumbNailImgUrl,
                     Integer qty, Integer discRate, Integer origPrice, Integer price,
                     Integer discPrice, Integer sellPrice, Integer shipFee) {
        this.item = item;
        this.productName = productName;
        this.thumbNailImgUrl = thumbNailImgUrl;
        this.qty = qty;
        this.discRate = discRate;
        this.origPrice = origPrice; // 원가
        this.price = price; // 정상가격(원가) + 추가금액
        this.discPrice = discPrice; // 할인 금액
        this.sellPrice = sellPrice; // 할인적용된 원가
        this.shipFee = shipFee;
    }

    public static OrderItem create(Item item, Integer qty) {
        if (qty == null || qty < 1) {
            throw new InvalidOrderItemQtyException(ORDER_ITEM_QTY_INVALID);
        }

        log.info("Create OrderItem, item.getTotalPrice={}, qty={}", item.getTotalPrice(), qty);

        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .productName(item.getProduct().getName())
                .thumbNailImgUrl(item.getThumbNailImgUrl())
                .qty(qty)
                .origPrice(item.getProduct().getOrigPrice())
                .price(item.getTotalPrice())
                .sellPrice(item.getProduct().getSellPrice())
                .shipFee(item.getProduct().getShipFee() != null ? item.getProduct().getShipFee() : 0)
                .build();

        // 할인 금액 계산
        orderItem.calculateDiscPrice(orderItem.sellPrice);

        return orderItem;
    }

    private void calculateDiscPrice(Integer sellPrice) {
        // 할인 금액 계산
        discPrice = this.origPrice - sellPrice; // 원가 - 할인 적용된 금액

        // 할인 금액이 음수인 경우 예외 발생
        if (discPrice < 0) {
            throw new DiscountPriceException(DISCOUNT_PRICE_INVALID);
        }
    }
}
