package shoppingmall.ankim.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.exception.CategoryNameTooLongException;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.product.exception.ProductNameTooLongException;

import java.util.ArrayList;
import java.util.List;

import static shoppingmall.ankim.domain.product.entity.ProductSellingStatus.*;
import static shoppingmall.ankim.global.exception.ErrorCode.CATEGORY_NAME_TOO_LONG;
import static shoppingmall.ankim.global.exception.ErrorCode.PRODUCT_NAME_TOO_LONG;

/*
    * 상품 정책
    * 상품명 -> 최대 60자까지
    * 상세설명 -> 50자까지

 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product {
    private static final int NAME_MAX_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

//    @Column(nullable = true) // FIXME 관리자 도메인 생성 후 연결해야함
//    private Long admNo;

    // 상품 : 카테고리 = N : 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_no")
    private Category category;

    // 이미지 리스트 필드 추가
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImg> productImgs = new ArrayList<>();

    // 품목 리스트 필드 추가
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    // 옵션 그룹 리스트 필드 추가
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionGroup> optionGroups = new ArrayList<>();

    private String name; // 상품명

    private String code; // 상품코드

    @Lob
    private String desc; // 상세설명

    @Column
    private Integer discRate; // 할인율

    @Column
    private Integer sellPrice; // 판매가격 (할인율 적용)

    @Column
    private Integer origPrice; // 정상가격

    @Column
    private String optYn; // 옵션여부

    @Column
    private String restockYn; // 재입고알림여부

    private Integer qty; // 재고량 -> 옵션없을 경우 (단품)

    @Enumerated(EnumType.STRING)
    private ProductSellingStatus sellingStatus; // 판매 상태

    private String bestYn; // 베스트 상품 여부

    private String freeShip; // 무료배송 여부

    private Integer shipFee; // 배송비

    private String searchKeywords; // 검색 키워드

    private String relProdCode; // 연관 상품코드

    @Lob
    private String cauProd; // 상품 유의사항

    @Lob
    private String cauOrd; // 주문 유의사항

    @Lob
    private String cauShip; // 배송 유의사항

    private Integer avgR; // 평균 별점
    private Integer wishCnt; // 찜횟수
    private Integer viewCnt; // 조회수
    private Integer rvwCnt; // 리뷰수
    private Integer qnaCnt; // 문의수
    private Integer dispOrd; // 노출 우선순위

    @Builder
    private Product(Category category, List<ProductImg> productImgs, List<Item> items, List<OptionGroup> optionGroups, String name, String code,
                   String desc, Integer discRate, Integer origPrice,
                   String optYn, String restockYn, Integer qty, ProductSellingStatus sellingStatus, String bestYn,
                   String freeShip, Integer shipFee, String searchKeywords, String relProdCode, String cauProd, String cauOrd, String cauShip) {
        this.category = category;
        this.productImgs = productImgs != null ? productImgs : new ArrayList<>();
        this.items = items != null ? items : new ArrayList<>();
        this.optionGroups = optionGroups != null ? optionGroups : new ArrayList<>();
        validateName(name);
        this.code = code;
        this.desc = desc;
        this.origPrice = origPrice;
        this.discRate = discRate;
        this.sellPrice = calculateSellPrice(origPrice, discRate); // 판매가 계산
        this.optYn = optYn;
        this.restockYn = restockYn;
        this.qty = qty;
        this.sellingStatus = sellingStatus;
        this.bestYn = bestYn;
        this.freeShip = freeShip;
        this.shipFee = shipFee;
        this.searchKeywords = searchKeywords;
        this.relProdCode = relProdCode;
        this.cauProd = cauProd;
        this.cauOrd = cauOrd;
        this.cauShip = cauShip;
    }

    // 판매가 계산 메서드
    private Integer calculateSellPrice(Integer origPrice, Integer discRate) {
        return origPrice - (origPrice * discRate / 100);
    }

    public static Product create(
          Category category,String name, String code, String desc
        , Integer discRate, Integer origPrice, String optYn, String restockYn
        , Integer qty, String bestYn, String freeShip, Integer shipFee, String searchKeywords, String relProdCode
        , String cauProd, String cauOrd, String cauShip)
    {
        return Product.builder()
                .category(category)
                .sellingStatus(SELLING)
                .name(name)
                .code(code)
                .desc(desc)
                .discRate(discRate)
                .origPrice(origPrice)
                .optYn(optYn)
                .restockYn(restockYn)
                .qty(qty)
                .bestYn(bestYn)
                .freeShip(freeShip)
                .shipFee(shipFee)
                .searchKeywords(searchKeywords)
                .relProdCode(relProdCode)
                .cauOrd(cauOrd)
                .cauProd(cauProd)
                .cauShip(cauShip)
                .build();
    }

    // 상품 이미지 추가 메서드
    public void addProductImg(ProductImg productImg) {
        this.productImgs.add(productImg);
    }

    // 품목 추가 메서드
    public void addItem(Item item) {
        this.items.add(item);
    }

    // 옵션 그룹 추가 메서드
    public void addOptionGroup(OptionGroup optionGroup) {
        this.optionGroups.add(optionGroup);
    }

    // 상품 이미지 제거 메서드
    public void removeProductImg(ProductImg productImg) {
        this.productImgs.remove(productImg);
    }

    // 품목 제거 메서드
    public void removeItem(Item item) {
        this.items.remove(item);
    }

    // 옵션 그룹 제거 메서드
    public void removeOptionGroup(OptionGroup optionGroup) {
        this.optionGroups.remove(optionGroup);
    }

    // 상품명 길이 유효성 검사 메서드
    private void validateName(String name) {
        if (name == null || name.length() > NAME_MAX_LENGTH) {
            throw new ProductNameTooLongException(PRODUCT_NAME_TOO_LONG);
        }
        this.name = name;
    }
}
