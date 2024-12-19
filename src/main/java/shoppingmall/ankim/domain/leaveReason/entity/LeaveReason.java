package shoppingmall.ankim.domain.leaveReason.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "leave_rsn")
public class LeaveReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y"; // 활성화 상태

    @Builder
    public LeaveReason(Long no, String reason, String activeYn) {
        this.no = no;
        this.reason = reason;
        this.activeYn = activeYn;
    }
}
