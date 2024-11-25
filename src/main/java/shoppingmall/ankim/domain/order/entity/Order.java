package shoppingmall.ankim.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.address.entity.admin.AdminAddress;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_no")
    private Long ordNo;

    @Column(name = "ord_code", length = 19)
    private String ordCode;

    @ManyToOne
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "addr_no", nullable = false)
    private AdminAddress adminAddress;

    @Column(name = "prod_name", length = 100)
    private String prodName;

    @Column(name = "prod_path", length = 500)
    private String prodPath;

    @Column(name = "total_qty", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer totalQty = 1;

    @Column(name = "total_price", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalPrice = 0;

    @Column(name = "total_ship_fee", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalShipFee = 0;

    @Column(name = "total_disc_price", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalDiscPrice = 0;

    @Column(name = "pay_amt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer payAmt = 0;

    @Column(name = "reg_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate = LocalDateTime.now();

}