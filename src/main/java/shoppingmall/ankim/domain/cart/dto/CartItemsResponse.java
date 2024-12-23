package shoppingmall.ankim.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemsResponse {

    private Long cartNo; // 장바구니 번호 (CartItem)
    private Long cartItemNo; // 장바구니 품목 번호 (CartItem)
    private Long itemNo; // 품목 번호(Item)
    private String productName; // 상품명 (Product)
    private String itemName; // 품목명 (CartItem)
    private String thumbNailImgUrl; // 썸네일 이미지 (CartItem) -> (Item)
    private Integer qty; // 주문 수량 (CartItem)
    private Integer itemQty; // 품목 재고량 (Item)
    private ProductSellingStatus sellingStatus; // 판매 상태 (Item)
    private Integer origPrice; // 정상가격 (Product)
    private Integer discRate; // 할인율 (Product)
    private Integer sellPrice; // 판매가격 (Product)
    private Integer addPrice; // 추가금액(품목에 대한 추가금) (Item)
    private Integer totalPrice; // 판매가(할인률 적용된 가격) + 추가금액 (Item)
    private String freeShip; // 무료배송 여부 (Product)
    private Integer shipFee; // 배송비 (Product)
    private Integer maxQty; // 최대 구매 수량 (Item)
    private Integer minQty; // 최소 구매 수량 (Item)

    public static CartItemsResponse of(Cart cart, CartItem cartItem) {
        return CartItemsResponse.builder()
                .cartNo(cart.getNo())
                .cartItemNo(cartItem.getNo())
                .itemNo(cartItem.getItem().getNo())
                .productName(cartItem.getProduct().getName())
                .itemName(cartItem.getItemName())
                .thumbNailImgUrl(cartItem.getItem().getThumbNailImgUrl())
                .qty(cartItem.getQty())
                .itemQty(cartItem.getItem().getQty())
                .sellingStatus(cartItem.getItem().getSellingStatus())
                .origPrice(cartItem.getProduct().getOrigPrice())
                .discRate(cartItem.getProduct().getDiscRate())
                .sellPrice(cartItem.getProduct().getSellPrice())
                .addPrice(cartItem.getItem().getAddPrice())
                .totalPrice(cartItem.getItem().getTotalPrice())
                .freeShip(cartItem.getProduct().getFreeShip())
                .shipFee(cartItem.getProduct().getShipFee())
                .maxQty(cartItem.getItem().getMaxQty())
                .minQty(cartItem.getItem().getMinQty())
                .build();
    }

}
