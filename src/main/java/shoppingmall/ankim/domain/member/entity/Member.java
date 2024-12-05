package shoppingmall.ankim.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.global.audit.Authority;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", indexes = {
        @Index(name = "idx_member_uuid", columnList = "uuid")
})
@ToString(of = {"loginId", "name"})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // PK 번호

    //    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @Column(columnDefinition = "BINARY(16)") // UUID 용량 줄이는 로직 추가를 위해 일단 null값 허용하도록 변경
    /*    BINARY(16)으로 변환해주는 컨버터 필요    */
    private UUID uuid;

    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    private String loginId; // 아이디(이메일)

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
    private Integer grade; // 회원 등급(기본 : 50 / GREEN)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    // 약관동의
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TermsHistory> termsHistory = new ArrayList<>();

    // 로그인 시도
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLoginAttempt> loginAttempts = new ArrayList<>();

//    @Transient
//    Authority authority;

    @Builder
    public Member(Long no,UUID uuid, String loginId, String pwd, String name,
                  String phoneNum, LocalDate birth, String gender,
                  LocalDateTime joinDate, Integer grade,
                  MemberStatus status, Authority authority,
                  List<Terms> termsList
    ) {
        this.no = no;
        this.uuid = uuid;
        this.loginId = loginId;
        this.pwd = pwd;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
        this.joinDate = LocalDateTime.now();
        this.grade = (grade == null || grade == 0) ? 50 : grade; // 기본 등급 설정
        this.status = status;
//        this.authority = authority;

        // termsList -> termsHistory 변환 및 설정
        this.termsHistory = (termsList != null) ?
                termsList.stream()
                        .map(terms -> new TermsHistory(this, terms, "Y", this.joinDate)) // agreeYn = "Y", agreeDate = joinDate
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }

    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    public void lock() {
        this.status = MemberStatus.LOCKED;
    }

    public void changePassword(String newPassword) {
        this.pwd = newPassword;
    }

}