package shoppingmall.ankim.domain.address.entity.admin;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.global.audit.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin_addr")
public class AdminAddress extends BaseEntity {

    @Id
    private Long adminNo; // Admin의 기본 키와 동일한 pk

    @OneToOne
    @MapsId // Admin의 pk를 사용
    @JoinColumn(name = "admin_no") // 외래 키와 연결
    private Admin admin;

    @Embedded
    private BaseAddress baseAddress;

    // 팩토리 메서드
    public static AdminAddress create(Admin admin, BaseAddress baseAddress) {
        AdminAddress adminAddress = new AdminAddress();
        adminAddress.admin = admin; // 연관 관계 설정
        adminAddress.baseAddress = baseAddress;
        return adminAddress;
    }

}