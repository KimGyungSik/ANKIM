package shoppingmall.ankim.domain.address.entity.member;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.admin.AdminAddress;
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

        @Column(name = "receiver", length = 20)
        private String receiver;        // 수령인

        @Column(name = "addr_name", length = 20)
        private String addressName; // 배송지명

        @Embedded
        private BaseAddress baseAddress; // 우편번호, 주소, 상세주소, 배송지 등록일, 배송지 수정일

        @Column(name = "phone_num", length = 20, nullable = false)
        private String phoneNumber; // 기본 연락처( 연락처1 )

        @Column(name = "phone_emgcy", length = 20)
        private String emergencyPhoneNumber; // 비상 연락처( 연락처2 )

        @Column(name = "addr_def")
        private String defaultAddressYn; // 기본 배송지 여부

        @Column(name = "active_yn", nullable = false)
        private String activeYn = "N"; // 활성 상태

        @Builder
        public MemberAddress(Long no, Member member,String receiver, String addressName, BaseAddress baseAddress, String phoneNumber, String emergencyPhoneNumber, String defaultAddressYn, String activeYn) {
                this.no = no;
                this.member = member;
                this.receiver = receiver;
                this.addressName = addressName;
                this.baseAddress = baseAddress;
                this.phoneNumber = phoneNumber;
                this.emergencyPhoneNumber = emergencyPhoneNumber;
                this.defaultAddressYn = defaultAddressYn == null ? "N" : defaultAddressYn;
                this.activeYn = activeYn == null ? "Y" : activeYn;
        }

        // 팩토리 메서드
        public static MemberAddress create(Member member, BaseAddress baseAddress) {
                MemberAddress memberAddress = new MemberAddress();
                memberAddress.member = member;
                memberAddress.baseAddress = baseAddress;
                return memberAddress;
        }

        // 연관 관계 설정 메서드
        public void registerAddress(BaseAddress baseAddress) {
                this.baseAddress = baseAddress;
        }

        // 팩토리 메서드
        public static MemberAddress create(Member member, String addressName, BaseAddress baseAddress, String phoneNumber, String emergencyPhoneNumber, String defaultAddressYn) {
                return MemberAddress.builder()
                        .member(member)
                        .addressName(addressName)
                        .baseAddress(baseAddress)
                        .phoneNumber(phoneNumber)
                        .emergencyPhoneNumber(emergencyPhoneNumber)
                        .defaultAddressYn(defaultAddressYn)
                        .build();
        }

        // 수령인 추가
        public static MemberAddress create(Member member, String receiver, String addressName, BaseAddress baseAddress, String phoneNumber, String emergencyPhoneNumber, String defaultAddressYn) {
                return MemberAddress.builder()
                        .member(member)
                        .receiver(receiver)
                        .addressName(addressName)
                        .baseAddress(baseAddress)
                        .phoneNumber(phoneNumber)
                        .emergencyPhoneNumber(emergencyPhoneNumber)
                        .defaultAddressYn(defaultAddressYn)
                        .build();
        }
}
