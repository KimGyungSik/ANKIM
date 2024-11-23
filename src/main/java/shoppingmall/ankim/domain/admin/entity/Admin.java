package shoppingmall.ankim.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.admin.AdminAddress;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 자동 증가 ID

    @Column(name = "loginId", nullable = false, unique = true, length = 20)
    private String loginId; // 관리자 ID

    @Column(name = "pwd", nullable = false, length = 200)
    private String pwd; // 비밀번호 (암호화)

    @Column(name = "name", nullable = false, length = 20)
    private String name; // 이름

    @Column(name = "email", length = 50)
    private String email; // 이메일

    @Column(name = "phone_num", length = 20)
    private String phoneNum; // 휴대폰 번호

    @Column(name = "office_num", length = 20)
    private String officeNum; // 사무실 번호

    @Column(name = "birth")
    private LocalDate birth; // 생년월일

    @Column(name = "gender", length = 1)
    private String gender; // 성별 (F/M)

    @Column(name = "join_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDate joinDate; // 가입 날짜

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private AdminStatus status = AdminStatus.ACTIVE; // 상태 (활성 / 퇴사 / 휴직 / 잠김)

    @Column(name = "mod_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDate modDate; // 수정 날짜

    @OneToOne(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdminAddress adminAddress;

    @Builder
    public Admin(Long no, String loginId, String pwd, String name, String email, String phoneNum, String officeNum, LocalDate birth, String gender, LocalDate joinDate, AdminStatus status, LocalDate modDate, AdminAddress adminAddress) {
        LocalDate localDate = LocalDate.now();
        this.no = no;
        this.loginId = loginId;
        this.pwd = pwd;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.officeNum = officeNum;
        this.birth = birth;
        this.gender = gender;
        this.joinDate = joinDate != null ? joinDate : localDate;
        this.status = status != null ? status : AdminStatus.ACTIVE;
        this.modDate = modDate != null ? modDate : localDate;
        this.adminAddress = adminAddress;
    }

    // 연관 관계 설정 메서드
    public void registerAddress(BaseAddress baseAddress) {
        this.adminAddress = AdminAddress.create(this, baseAddress);
    }

    public void activate() {
        this.status = AdminStatus.ACTIVE;
    }

    public void lock() {
        this.status = AdminStatus.LOCKED;
    }
}