package shoppingmall.ankim.domain.orderItem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

@Getter
@NoArgsConstructor
public class OrderItemResponse {
    private String productName; // 상품명
    private Long itemId; // 품목 번호
    private String code; // 품목코드
    private String name; // 품목명
    private String thumbNailImgUrl; // 썸네일 이미지
    private Integer addPrice; // 추가금액
    private Integer discPrice; // 최종결제금액
    private Integer qty; // 주문수량
    private ProductSellingStatus sellingStatus; // 판매 상태

    @Builder
    public OrderItemResponse(String productName, Long itemId, String code, String name, String thumbNailImgUrl, Integer addPrice, Integer discPrice, Integer qty, ProductSellingStatus sellingStatus) {
        this.productName = productName;
        this.itemId = itemId;
        this.code = code;
        this.name = name;
        this.thumbNailImgUrl = thumbNailImgUrl;
        this.addPrice = addPrice;
        this.discPrice = discPrice;
        this.qty = qty;
        this.sellingStatus = sellingStatus;
    }

    public static OrderItemResponse of(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productName(orderItem.getProductName())
                .itemId(orderItem.getItem().getNo())
                .code(orderItem.getItem().getCode())
                .name(orderItem.getItem().getName())
                .thumbNailImgUrl(orderItem.getThumbNailImgUrl())
                .addPrice(orderItem.getItem().getAddPrice())
                .discPrice(orderItem.getDiscPrice())
                .qty(orderItem.getItem().getQty())
                .sellingStatus(orderItem.getItem().getSellingStatus())
                .build();
    }
}
