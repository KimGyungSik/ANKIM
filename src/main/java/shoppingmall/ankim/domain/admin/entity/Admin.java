package shoppingmall.ankim.domain.admin.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 자동 증가 ID

    @Column(name = "id", nullable = false, unique = true, length = 20)
    private String id; // 관리자 ID

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
}