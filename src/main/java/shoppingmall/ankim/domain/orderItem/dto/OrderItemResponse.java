package shoppingmall.ankim.domain.orderItem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.exception.FinalPriceException;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import static shoppingmall.ankim.global.exception.ErrorCode.FINAL_PRICE_INVALID;

@Getter
@NoArgsConstructor
public class OrderItemResponse {
    private String productName; // 상품명
    private Long itemId; // 품목 번호
    private String code; // 품목코드
    private String name; // 품목명
    private String thumbNailImgUrl; // 썸네일 이미지
    private Integer price; // 원가 + 추가금
    private Integer discPrice; // 할인 금액
    private Integer finalPrice; // 최종 결제 금액
    private Integer qty; // 개별 주문 수량
    private ProductSellingStatus sellingStatus; // 판매 상태

    @Builder
    public OrderItemResponse(String productName, Long itemId, String code, String name,
                             String thumbNailImgUrl, Integer price, Integer discPrice, Integer finalPrice,
                             Integer qty, ProductSellingStatus sellingStatus) {
        this.productName = productName;
        this.itemId = itemId;
        this.code = code;
        this.name = name;
        this.thumbNailImgUrl = thumbNailImgUrl;
        this.price = price;
        this.discPrice = discPrice;
        this.finalPrice = finalPrice;
        this.qty = qty;
        this.sellingStatus = sellingStatus;
    }

    public static OrderItemResponse of(OrderItem orderItem) {
        OrderItemResponse response = OrderItemResponse.builder()
                .productName(orderItem.getProductName())
                .itemId(orderItem.getItem().getNo())
                .code(orderItem.getItem().getCode())
                .name(orderItem.getItem().getName())
                .thumbNailImgUrl(orderItem.getThumbNailImgUrl())
                .price(orderItem.getItem().getTotalPrice())
                .discPrice(orderItem.getDiscPrice())
                .qty(orderItem.getQty())
                .sellingStatus(orderItem.getItem().getSellingStatus())
                .build();

        response.calculateFinalPrice();

        return response;
    }

    private void calculateFinalPrice() {
        // 할인 금액 계산
        finalPrice = this.price - discPrice; // 원가 + 추가금 - 할인 적용된 금액

        // 최종 결제할 금액이 음수인 경우 예외 발생
        if (finalPrice < 0) {
            throw new FinalPriceException(FINAL_PRICE_INVALID);
        }
    }

}
