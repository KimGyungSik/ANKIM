package shoppingmall.ankim.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private Long cateNo;

    @Column(nullable = false)
    private Long admNo;

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
