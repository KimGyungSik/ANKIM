package shoppingmall.ankim.domain.address.entity.admin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.global.audit.BaseEntity;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin_addr")
public class AdminAddress extends BaseEntity {

    @Id
    @Column(name = "admin_no")
    private Long adminNo;

    @MapsId
    @OneToOne
    @JoinColumn(name = "admin_no")
    private Admin admin;

    @Embedded
    private BaseAddress baseAddress;

}