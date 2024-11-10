package shoppingmall.ankim.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.option.entity.OptionGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private Long admNo;

    // 상품 : 카테고리 = N : 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_no", nullable = false)
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

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String code;

    @Lob
    private String desc;

    @Column(precision = 5, scale = 2)
    private BigDecimal discRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal sellPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal origPrice;

    @Column(columnDefinition = "CHAR(1)")
    private String optYn;

    private Integer optCnt;

    @Column(length = 500)
    private String searchKeywords;

    @Lob
    private String cauProd;

    @Lob
    private String cauOrd;

    @Lob
    private String cauShip;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgR;

    private Integer wishCnt;
    private Integer viewCnt;
    private Integer rvwCnt;
    private Integer qnaCnt;
    private Integer dispOrd;
    private String stockSt;
    private Integer shipFee;
    private String freeShip;
    private String relProdNo;
    private String bestYn;
    private Integer restockCnt;

    // Getters and Setters
}
