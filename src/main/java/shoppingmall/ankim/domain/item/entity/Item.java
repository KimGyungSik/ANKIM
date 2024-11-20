package shoppingmall.ankim.domain.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.itemOption.entity.ItemOption;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.product.entity.ProductSellingStatus.*;

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

    private String code; // 품목코드 -> 상품코드 + 1 을 붙인 것

    private String name; // 품목명 -> 옵션 그룹 + 옵션 값

    @Column(name = "add_price", precision = 10, scale = 2)
    private Integer addPrice; // 추가금액

    private Integer totalPrice; // 판매가(할인률 적용된 가격) + 추가금액

    private Integer qty; // 재고량

    @Column(name = "saf_qty")
    private Integer safQty; // 안전재고량

    @Enumerated(EnumType.STRING)
    private ProductSellingStatus sellingStatus; // 판매 상태

    @Column(name = "max_qty")
    private Integer maxQty; // 최대 구매 수량

    @Column(name = "min_qty")
    private Integer minQty; // 최소 구매 수량

    @Builder
    private Item(Product product, List<OptionValue> optionValues, String code, String name, Integer addPrice,
                 Integer qty, Integer safQty, ProductSellingStatus sellingStatus, Integer maxQty, Integer minQty) {
        this.product = product;
        this.code = code;
        this.name = name;
        this.addPrice = addPrice;
        this.qty = qty;
        this.safQty = safQty;
        this.sellingStatus = sellingStatus;
        this.maxQty = maxQty;
        this.minQty = minQty;
        addItemOptions(optionValues); // ItemOption 관계 추가
        this.totalPrice = product.getSellPrice() + addPrice; // 총 가격 추가
    }

    // 옵션 값에 따라 ItemOption을 생성하고 리스트에 추가
    private void addItemOptions(List<OptionValue> optionValues) {
        for (OptionValue optionValue : optionValues) {
            this.itemOptions.add(new ItemOption(this, optionValue));
        }
    }

    public static Item create(Product product, List<OptionValue> optionValues, String code, String name, Integer addPrice,
                              Integer qty, Integer safQty, Integer maxQty, Integer minQty) {
        return Item.builder()
                .sellingStatus(SELLING)
                .product(product)
                .optionValues(optionValues)
                .code(code)
                .name(name)
                .addPrice(addPrice)
                .qty(qty)
                .safQty(safQty)
                .maxQty(maxQty)
                .minQty(minQty)
                .build();
    }

    public void change(ItemDetailServiceRequest updatedItem) {
        // 추가 가격
        if (updatedItem.getAddPrice() != null) {
            this.addPrice = updatedItem.getAddPrice();
        }

        // 재고량
        if (updatedItem.getQty() != null) {
            this.qty = updatedItem.getQty();
        }

        // 판매 상태
        if (updatedItem.getSellingStatus() != null) {
            this.sellingStatus = updatedItem.getSellingStatus();
        }

        // 안전 재고량
        if (updatedItem.getSafQty() != null) {
            this.safQty = updatedItem.getSafQty();
        }

        // 최대 주문 가능 수량
        if (updatedItem.getMaxQty() != null) {
            this.maxQty = updatedItem.getMaxQty();
        }

        // 최소 주문 가능 수량
        if (updatedItem.getMinQty() != null) {
            this.minQty = updatedItem.getMinQty();
        }
    }



    public boolean isQuantityLessThan(int qty) {
        return this.qty < qty;
    }

    public void deductQuantity(int qty) {
        if (isQuantityLessThan(qty)) {
            throw new IllegalArgumentException("차감할 재고 수량이 없습니다."); // FIXME
        }
        this.qty -= qty;
    }
}