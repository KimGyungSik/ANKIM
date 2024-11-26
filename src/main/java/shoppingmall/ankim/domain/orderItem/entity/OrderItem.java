package shoppingmall.ankim.domain.orderItem.entity;


import jakarta.persistence.*;
import lombok.*;
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

    private Integer qty; // 주문 수량

    private Integer price; // 주문 상품 가격, 정상가격(원가) + 추가금액

    private Integer discPrice; // 주문 할인 가격 = price(원가 + 추가금액) - sellPrice (할인 적용된 금액)

    private Integer shipFee; // 배송비


    @Builder
    private OrderItem(Item item, String productName, String thumbNailImgUrl,
                     Integer qty, Integer price,
                     Integer shipFee, Integer discPrice) {
        this.item = item;
        this.productName = productName;
        this.thumbNailImgUrl = thumbNailImgUrl;
        this.qty = qty;
        this.price = price; // 정상가격(원가) + 추가금액
        this.discPrice = calculateDiscPrice(item.getProduct().getSellPrice());
        this.shipFee = shipFee;
        this.discPrice = discPrice;
    }

    public static OrderItem create(Item item, Integer qty) {
        if (qty == null || qty < 1) {
            throw new InvalidOrderItemQtyException(ORDER_ITEM_QTY_INVALID);
        }

        return OrderItem.builder()
                .item(item)
                .productName(item.getProduct().getName())
                .thumbNailImgUrl(item.getThumbNailImgUrl())
                .qty(qty)
                .price(item.getTotalPrice())
                .shipFee(item.getProduct().getShipFee())
                .build();
    }

    private Integer calculateDiscPrice(Integer sellPrice) {
        // 할인 금액 계산
        Integer discPrice = this.price - sellPrice;

        // 할인 금액이 음수인 경우 예외 발생
        if (discPrice < 0) {
            throw new DiscountPriceException(DISCOUNT_PRICE_INVALID);
        }

        return discPrice;
    }


}
