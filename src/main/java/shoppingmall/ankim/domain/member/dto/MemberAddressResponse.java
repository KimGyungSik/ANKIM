package shoppingmall.ankim.domain.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;

@Data
@NoArgsConstructor
public class MemberAddressResponse {
    private Integer zipCode;
    private String addressMain;
    private String addressDetail;

    @Builder
    public MemberAddressResponse(Integer zipCode, String addressMain, String addressDetail) {
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
    }

    public static MemberAddressResponse of(MemberAddress memberAddress) {
        return MemberAddressResponse.builder()
                .zipCode(memberAddress.getBaseAddress().getZipCode())
                .addressMain(memberAddress.getBaseAddress().getAddressMain())
                .addressDetail(memberAddress.getBaseAddress().getAddressDetail())
                .build();
    }
}
