package shoppingmall.ankim.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.processing.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@Table(name = "member", indexes = {
        @Index(name = "idx_member_uuid", columnList = "uuid")
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
/*    BINARY(16)으로 변환해주는 컨버터 필요    */
    private UUID uuid;

    @Column(nullable = false, length = 50, unique = true)
    private String id;

    @Column(nullable = false, length = 200)
    private String pwd;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String phoneNum;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "first_ord_date")
    private LocalDateTime firstOrderDate;

    @Column
    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

}