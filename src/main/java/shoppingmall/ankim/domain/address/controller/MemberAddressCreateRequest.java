package shoppingmall.ankim.domain.address.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.dto.MemberAddressCreateServiceRequest;

@Data
@NoArgsConstructor
public class MemberAddressCreateRequest {

    private String addressName;         // 주소 이름
    @NotNull(message = "우편번호를 입력해주세요.")
    private Integer zipCode; // 우편번호
    @NotBlank(message = "주소를 선택해주세요.")
    private String addressMain; // 주소
    private String addressDetail;       // 상세 주소
    private String phoneNumber;         // 전화번호
    private String emergencyPhoneNumber; // 비상 전화번호
    private String defaultAddressYn;    // 기본 주소 여부

    // Convert to ServiceRequest
    public MemberAddressCreateServiceRequest toServiceRequest() {
        return MemberAddressCreateServiceRequest.builder()
                .addressName(this.addressName)
                .zipCode(this.zipCode)
                .addressMain(this.addressMain)
                .addressDetail(this.addressDetail)
                .phoneNumber(this.phoneNumber)
                .emergencyPhoneNumber(this.emergencyPhoneNumber)
                .defaultAddressYn(this.defaultAddressYn)
                .build();
    }
}

