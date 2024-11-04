package shoppingmall.ankim.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "ord_no", nullable = false)
    private Long ordNo;

    @Column(name = "trck_no")
    private String trckNo;

    private String courier;
    private String status;
    private String receiver;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    private String address;
    private String zipcode;

    @Column(name = "del_req")
    private String delReq;
}