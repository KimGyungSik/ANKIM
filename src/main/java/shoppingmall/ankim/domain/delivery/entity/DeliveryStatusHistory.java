package shoppingmall.ankim.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DeliveryStatusHistory")
public class DeliveryStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "del_no", nullable = false)
    private Long delNo;

    private String status;

    @Column(name = "change_at")
    private LocalDateTime changeAt;
    private String reason;
}
