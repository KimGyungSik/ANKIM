package shoppingmall.ankim.domain.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.itemOption.entity.ItemOption;
import shoppingmall.ankim.domain.product.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    // 상품 : 품목 = 1 : N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_no", nullable = false)
    private Product product;

    // 품목옵션에 대한 필드 리스트
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOption> itemOptions = new ArrayList<>();

    private String code;

    private String name;

    @Column(name = "add_price", precision = 10, scale = 2)
    private BigDecimal addPrice;

    private Integer qty;

    @Column(name = "saf_qty")
    private Integer safQty;

    @Column(name = "sell_st")
    private String sellSt;

    @Column(name = "max_qty")
    private Integer maxQty;

    @Column(name = "min_qty")
    private Integer minQty;
}