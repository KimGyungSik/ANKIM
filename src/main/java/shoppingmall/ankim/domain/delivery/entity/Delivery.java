package shoppingmall.ankim.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Setter
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "trck_no")
    private String trckNo; // 송장 번호

    private String courier; // 택배사

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // 배송 상태

    private String receiver; // 수령인 이름

    @Column(name = "receiver_phone")
    private String receiverPhone; // 수령인 전화번호

    private String address; // 수령인 주소
    private Integer zipcode; // 우편번호

    @Column(name = "del_req")
    private String delReq; // 배송 요청사항

    @Builder
    private Delivery(String trckNo, String courier, DeliveryStatus status, String receiver, String receiverPhone, String address, Integer zipcode, String delReq) {
        this.trckNo = trckNo;
        this.courier = courier;
        this.status = status;
        this.receiver = receiver;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.zipcode = zipcode;
        this.delReq = delReq;
    }
    public static Delivery create(MemberAddress memberAddress, String courier, String delReq, TrackingNumberGenerator trackingNumberGenerator) {
        BaseAddress baseAddress = memberAddress.getBaseAddress();
        String trckNo = trackingNumberGenerator.generate(); // 주입받은 생성기 사용

        return Delivery.builder()
                .trckNo(trckNo) // 송장 번호 생성 로직
                .courier(courier)
                .status(DeliveryStatus.PREPARING) // 기본 배송 상태
                .receiver(memberAddress.getMember().getName()) // 수령인 이름
                .receiverPhone(memberAddress.getPhoneNumber()) // 수령인 전화번호
                .address(baseAddress.getAddressMain() + " " + baseAddress.getAddressDetail()) // 주소 조합
                .zipcode(baseAddress.getZipCode()) // 우편번호
                .delReq(delReq) // 배송 요청사항
                .build();
    }

    // 배송 상태 취소
    public void cancel() {
        this.status = DeliveryStatus.CANCELED;
    }
}