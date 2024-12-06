package shoppingmall.ankim.domain.address.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberAddressCreateServiceRequest {

    private String addressName;         // 주소 이름
    private Integer zipCode;            // 우편번호
    private String addressMain;         // 메인 주소
    private String addressDetail;       // 상세 주소
    private String phoneNumber;         // 전화번호
    private String emergencyPhoneNumber; // 비상 전화번호
    private String defaultAddressYn;    // 기본 주소 여부

    @Builder
    public MemberAddressCreateServiceRequest(String addressName, Integer zipCode, String addressMain, String addressDetail,
                                             String phoneNumber, String emergencyPhoneNumber, String defaultAddressYn) {
        this.addressName = addressName;
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
        this.phoneNumber = phoneNumber;
        this.emergencyPhoneNumber = emergencyPhoneNumber;
        this.defaultAddressYn = defaultAddressYn != null ? defaultAddressYn : "N"; // 기본값 처리
    }
}
