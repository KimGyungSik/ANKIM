package shoppingmall.ankim.domain.memberHistory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mem_history")
public class MemberHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "mod_code", nullable = false)
    private ModificationCode modCode; // 수정 코드 (이름, 비밀번호, 연락처, 이메일, 회원상태)

    @Column(name = "old_val", length = 200)
    private String oldValue; // 이전 값

    @Column(name = "new_val", length = 200)
    private String newValue; // 새로운 값

    @Column(name = "mod_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modifiedDate = LocalDateTime.now(); // 수정 일자
}
