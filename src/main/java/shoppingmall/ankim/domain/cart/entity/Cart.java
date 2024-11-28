package shoppingmall.ankim.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 자동 증가 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호 (외래 키)

    @Column(name = "reg_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate; // 등록 날짜

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y"; // 활성화 상태

    // 장바구니 상품
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder
    public Cart(Long no, Member member, LocalDateTime regDate, String activeYn
            , List<CartItem> cartItems
    ) {
        this.no = no;
        this.member = member;
        this.regDate = regDate == null ? LocalDateTime.now() : regDate;
        this.activeYn = activeYn == null ? "Y" : activeYn;

        this.cartItems = cartItems == null ? new ArrayList<>() : cartItems;
    }

    public static Cart create(Member member, LocalDateTime regDate) {
        return Cart.builder()
                .member(member)
                .regDate(regDate)
                .build();
    }

    // 장바구니에 품목을 추가하는 메서드
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        if (cartItem.getCart() != this) {
            cartItem.addCart(this); // 양방향 관계 설정
        }
    }

    // 장바구니 비활성화 메서드 : 삭제하지 않고 활성상태를 변경한다.
    public void deactivate() {
        this.activeYn = "N";
        for (CartItem cartItem : cartItems) {
            cartItem.deactivate();
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }
}