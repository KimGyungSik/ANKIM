package shoppingmall.ankim.domain.address.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.global.audit.BaseEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mem_addr")
public class MemberAddress extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long no;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "mem_no", nullable = false)
        private Member member;

        @Column(name = "addr_name", length = 20, nullable = false)
        private String addressName;

        @Embedded
        private BaseAddress baseAddress;

        @Column(name = "phone_num", length = 20, nullable = false)
        private String phoneNumber;

        @Column(name = "phone_emgcy", length = 20)
        private String emergencyPhoneNumber;

        @Column(name = "addr_def", columnDefinition = "CHAR(1) DEFAULT 'N'")
        private String defaultAddressYn = "N";

        @Column(name = "active_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
        private String activeYn = "Y";

        // 팩토리 메서드
        public static MemberAddress create(Member member, String addressName, BaseAddress baseAddress, String phoneNumber, String emergencyPhoneNumber, String defaultAddressYn) {
                MemberAddress memberAddress = new MemberAddress();
                memberAddress.member = member;
                memberAddress.addressName = addressName;
                memberAddress.baseAddress = baseAddress;
                memberAddress.phoneNumber = phoneNumber;
                memberAddress.emergencyPhoneNumber = emergencyPhoneNumber;
                memberAddress.defaultAddressYn = defaultAddressYn != null ? defaultAddressYn : "N"; // 기본값 처리
                return memberAddress;
        }
}
