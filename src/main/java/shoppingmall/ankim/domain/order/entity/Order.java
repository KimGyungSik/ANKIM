package shoppingmall.ankim.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import shoppingmall.ankim.domain.address.entity.admin.AdminAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.delivery.entity.DeliveryStatus.*;
import static shoppingmall.ankim.domain.delivery.entity.DeliveryStatus.CANCELED;
import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2") // Hibernate UUID 생성 전략
    @Column(name = "ord_no", unique = true, nullable = false)
    private String ordNo;

    @Setter
    @Column(name = "ord_code", length = 19)
    private String ordCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_no")
    private Delivery delivery; // 배송 정보

    @Column(name = "total_qty",columnDefinition = "INT DEFAULT 1")
    private Integer totalQty = 1; // 총 주문 수량

    @Column(name = "total_price", columnDefinition = "INT DEFAULT 0")
    private Integer totalPrice = 0; // 총 상품 금액

    @Column(name = "total_ship_fee", columnDefinition = "INT DEFAULT 0")
    private Integer totalShipFee = 0; // 총 배송비

    @Column(name = "total_disc_price", columnDefinition = "INT DEFAULT 0")
    private Integer totalDiscPrice = 0; // 총 할인 금액

    @Column(name = "pay_amt", columnDefinition = "INT DEFAULT 0")
    private Integer payAmt = 0; // 최종 결제 금액

    @Column(name = "reg_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate = LocalDateTime.now(); // 주문 등록일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    @Column(name = "mod_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modDate = LocalDateTime.now(); // 주문 상태 변경일


    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    @Builder
    private Order(Member member, Delivery delivery, List<OrderItem> orderItems, LocalDateTime regDate, OrderStatus orderStatus) {
        this.member = member;

        // Delivery 설정
        if (delivery != null) {
            setDelivery(delivery);
        }

        // OrderItem 리스트 설정
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem orderItem : orderItems) {
                addOrderItem(orderItem);
            }
            this.totalQty = calculateTotalQty(orderItems);
            this.totalPrice = calculateTotalPrice(orderItems);
            this.totalDiscPrice = calculateTotalDiscPrice(orderItems);
            this.totalShipFee = calculateTotalShipFee(orderItems);
        } else {
            // 기본값 설정 (빈 리스트에 대한 방어 코드)
            this.totalQty = 0;
            this.totalPrice = 0;
            this.totalDiscPrice = 0;
            this.totalShipFee = 0;
        }

        // 기타 필드 초기화
        this.payAmt = totalPrice - totalDiscPrice;
        this.regDate = regDate;
        this.orderStatus = orderStatus;
    }

    public static Order create(List<OrderItem> orderItems, Member member, Delivery delivery, LocalDateTime regDate) {
        return Order.builder()
                .orderStatus(INIT)
                .member(member)
                .orderItems(orderItems)
                .delivery(delivery)
                .regDate(regDate)
                .build();
    }

    // 총 주문수량
    private Integer calculateTotalQty(List<OrderItem> items) {
        return items.stream()
                .mapToInt(OrderItem::getQty)
                .sum();
    }

    // 총 상품금액
    private Integer calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .mapToInt(OrderItem::getPrice)
                .sum();
    }

    // 총 배송비
    private Integer calculateTotalShipFee(List<OrderItem> items) {
        return items.stream()
                .mapToInt(OrderItem::getShipFee)
                .sum();
    }

    // 총 할인금액
    private Integer calculateTotalDiscPrice(List<OrderItem> items) {
        return items.stream()
                .mapToInt(OrderItem::getDiscPrice)
                .sum();
    }

    // 주문 취소 -> 배송 준비 상태일떄만 가능
    public void cancelOrder(Order order) {
        if (!delivery.getStatus().canCancel()) {
            throw new IllegalStateException("배송이 이미 시작되어 주문을 취소할 수 없습니다.");
        }

        // 주문 및 배송 취소 처리
        order.setOrderStatus(OrderStatus.CANCELED);
        delivery.cancel();
    }
}