package shoppingmall.ankim.domain.address.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;

@Data
@NoArgsConstructor
public class ExistAddressResponse {
    private Long addressNo;
    private String addressName; // 배송지명
    private String receiver; // 수령인
    private Integer zipCode; // 우편번호
    private String addressMain; // 기본 주소
    private String addressDetail; // 상세 주소
    private String phoneNumber; // 기본 연락처( 연락처1 )
    private String emergencyPhoneNumber; // 비상 연락처( 연락처2 )
    private String defaultAddressYn; // 기본 배송지 여부

    @Builder
    public ExistAddressResponse(Long addressNo, String addressName, String receiver, Integer zipCode, String addressMain,
                                String addressDetail, String phoneNumber, String emergencyPhoneNumber, String defaultAddressYn) {
        this.addressNo = addressNo;
        this.addressName = addressName;
        this.receiver = receiver;
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
        this.phoneNumber = phoneNumber;
        this.emergencyPhoneNumber = emergencyPhoneNumber;
        this.defaultAddressYn = defaultAddressYn;
    }

    public static ExistAddressResponse of(MemberAddress memberAddress) {
        return ExistAddressResponse.builder()
                .addressNo(memberAddress.getNo())
                .addressName(memberAddress.getAddressName())
                .receiver(memberAddress.getReceiver())
                .zipCode(memberAddress.getBaseAddress().getZipCode())
                .addressMain(memberAddress.getBaseAddress().getAddressMain())
                .addressDetail(memberAddress.getBaseAddress().getAddressDetail())
                .phoneNumber(memberAddress.getPhoneNumber())
                .emergencyPhoneNumber(memberAddress.getEmergencyPhoneNumber())
                .defaultAddressYn(memberAddress.getDefaultAddressYn())
                .build();
    }
}
