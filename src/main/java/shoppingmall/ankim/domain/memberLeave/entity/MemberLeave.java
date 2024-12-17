package shoppingmall.ankim.domain.memberLeave.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.member.entity.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mem_leave")
public class MemberLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_rsn_no", nullable = false)
    private LeaveReason leaveReason;

    @Column(name = "leave_msg", length = 100)
    private String leaveMsg; // 기타 사유일 경우 직접 입력

    @Column(name = "leave_at", updatable = false)
    private LocalDateTime leaveAt; // 탈퇴일

    @Builder
    public MemberLeave(Long no, Member member, LeaveReason leaveReason, String leaveMsg, LocalDateTime leaveAt) {
        this.no = no;
        this.member = member;
        this.leaveReason = leaveReason;
        this.leaveMsg = leaveMsg;
        this.leaveAt = leaveAt == null ? LocalDateTime.now() : leaveAt;
    }
}