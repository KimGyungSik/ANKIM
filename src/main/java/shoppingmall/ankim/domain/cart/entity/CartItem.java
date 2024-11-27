package shoppingmall.ankim.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.item.entity.Item;
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
    @JoinColumn(name = "item_no", nullable = false)
    private Item item; // 상품 품목 번호

    @Column(name="prod_name", nullable = false)
    private String prodName;

    @Column(name="prod_path", nullable = false)
    private String prodPath;

    @Column(name = "qty", nullable = false)
    private Integer qty; // 수량

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate = LocalDateTime.now(); // 추가일

    @Column(name = "active_yn", nullable = false)
    private String activeYn = "Y"; // 활성 상태

    @Builder
    public CartItem(Long no, Cart cart, Item item, Integer qty, String prodName, String prodPath, LocalDateTime regDate, String activeYn) {
        this.no = no;
        this.cart = cart;
        this.item = item;
        this.prodName = prodName;
        this.prodPath = prodPath;
        this.qty = qty;
        this.regDate = regDate == null? LocalDateTime.now() : regDate;
        this.activeYn = activeYn == null ? "Y" : activeYn;
    }

    public static CartItem create(Cart cart, Item item, Integer qty, LocalDateTime regDate) {

        return CartItem.builder()
                .cart(cart)
                .item(item)
                .prodName(item.getName())
                .prodPath(item.getThumbNailImgUrl())
                .qty(qty)
                .regDate(regDate == null ? LocalDateTime.now() : regDate)
                .build();
    }

    public void linkToCart(Cart cart) {
        this.cart = cart; // cart 필드 설정
    }

    public void updateQty(int qty) {
        this.qty = qty;
        this.regDate = LocalDateTime.now();
    }

    public void deactivate() {
        this.activeYn = "N";
    }
}