package shoppingmall.ankim.domain.address.service.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.admin.AdminAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AddressRegisterServiceRequest {
    private Integer zipCode; // 우편번호
    private String addressMain; // 주소
    private String addressDetail; // 상세주소

    @Builder
    public AddressRegisterServiceRequest(Integer zipCode, String addressMain, String addressDetail) {
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
    }


}
