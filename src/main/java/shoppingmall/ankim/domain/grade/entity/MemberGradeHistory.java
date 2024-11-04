package shoppingmall.ankim.domain.grade.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberGradeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호

    @ManyToOne
    @JoinColumn(name = "grade_no", nullable = false)
    private GradeCode gradeNo; // 등급 번호 (50 / 100 / 150 / 200)

    @Column(name = "grade_name", length = 20, nullable = false)
    private String gradeName; // 등급 이름

    @Column(name = "start_dt")
    private LocalDate startDate; // 시작 날짜

    @Column(name = "end_dt")
    private LocalDate endDate; // 종료 날짜

    @Column(name = "active_yn", columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String activeYn = "Y"; // 활성화 여부
}
