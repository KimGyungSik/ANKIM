package shoppingmall.ankim.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordNo;

    @ManyToOne
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @Column(name = "ord_code", length = 19)
    private String ordCode;

    @Column(name = "prod_name", length = 100)
    private String prodName;

    @Column(name = "prod_path", length = 500)
    private String prodPath;

    @Column(name = "total_qty", nullable = false)
    private Integer totalQty;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "total_ship_fee", nullable = false)
    private Integer totalShipFee;

    @Column(name = "total_disc_price", nullable = false)
    private Integer totalDiscPrice;

    @Column(name = "pay_amt", nullable = false)
    private Integer payAmt;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

}