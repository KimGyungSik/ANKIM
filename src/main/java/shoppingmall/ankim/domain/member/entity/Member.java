package shoppingmall.ankim.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.global.audit.Authority;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", indexes = {
        @Index(name = "idx_member_uuid", columnList = "uuid")
})
@ToString(of = {"id", "name"})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // PK 번호

    //    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @Column(columnDefinition = "BINARY(16)") // UUID 용량 줄이는 로직 추가를 위해 일단 null값 허용하도록 변경
    /*    BINARY(16)으로 변환해주는 컨버터 필요    */
    private UUID uuid;

    @Column(nullable = false, length = 50, unique = true)
    private String id; // 아이디(이메일)

    @Column(nullable = false, length = 200)
    private String pwd; // 비밀번호

    @Column(nullable = false, length = 20)
    private String name; // 이름

    @Column(name = "phone_num", nullable = false, length = 20)
    private String phoneNum; // 휴대전화번호

    @Column(nullable = false)
    private LocalDate birth; // 생년월일

    @Column(nullable = false, length = 1)
    private String gender; // 성별

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate; // 가입일

    @Column(name = "first_ord_date")
    private LocalDateTime firstOrderDate; // 첫 주문일

    @Column(nullable = false)
    private Integer grade; // 회원 등급

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

//    @Transient
//    Authority authority;

    @Builder
    public Member(UUID uuid, String id, String pwd, String name, String phoneNum, LocalDate birth, String gender, LocalDateTime joinDate, Integer grade, MemberStatus status, Authority authority) {
        this.uuid = uuid;
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
        this.joinDate = joinDate;
        this.grade = grade;
        this.status = status;
//        this.authority = authority;
    }

}