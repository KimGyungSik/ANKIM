package shoppingmall.ankim.domain.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "prod_no", nullable = false)
    private Long prodNo;

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