package shoppingmall.ankim.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_item")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_no", nullable = false)
    private Cart cart; // 장바구니 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_no", nullable = false)
    private Product product; // 상품 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_no", nullable = false)
    private Item item; // 상품 품목 번호

    @Column(name="item_name", nullable = false)
    private String itemName;

//    @Column(name="item_path", nullable = false)
//    private String thumbNailImgUrl;

    @Column(name = "qty", nullable = false)
    private Integer qty; // 수량

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate = LocalDateTime.now(); // 추가일

    @Column(name = "active_yn", nullable = false)
    private String activeYn = "Y"; // 활성 상태

    @Builder
    public CartItem(Long no, Cart cart, Product product, Item item, Integer qty, String itemName
//            , String thumbNailImgUrl
            , LocalDateTime regDate, String activeYn) {
        this.no = no;
        this.cart = cart;
        this.product = product;
        this.item = item;
        this.itemName = itemName;
//        this.thumbNailImgUrl = thumbNailImgUrl;
        this.qty = qty;
        this.regDate = regDate == null? LocalDateTime.now() : regDate;
        this.activeYn = activeYn == null ? "Y" : activeYn;
    }

    public static CartItem create(Cart cart, Product product, Item item, Integer qty, LocalDateTime regDate) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .item(item)
                .itemName(item.getName())
//                .thumbNailImgUrl(item.getThumbNailImgUrl())
                .qty(qty)
                .regDate(regDate == null ? LocalDateTime.now() : regDate)
                .build();
    }

    public void addCart(Cart cart) {
        this.cart = cart; // cart 필드 설정
    }

    // 장바구니에 동일 상품 조회시 사용하는 메서드
    public void updateQuantityWithDate(int qty) {
        this.qty = qty;
        this.regDate = LocalDateTime.now();
    }

    public void deactivate() {
        this.activeYn = "N";
    }

    // 장바구니 페이지에서 사용하는 단순 수량 변경 메서드
    public void changeQuantity(int qty) {
        this.qty = qty;
    }
}