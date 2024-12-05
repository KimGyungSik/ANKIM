package shoppingmall.ankim.domain.address.service.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.member.entity.Member;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberAddressRegisterServiceRequest {
    private Integer zipCode; // 우편번호
    private String addressMain; // 주소
    private String addressDetail; // 상세주소

    @Builder
    public MemberAddressRegisterServiceRequest(Integer zipCode, String addressMain, String addressDetail) {
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
    }

    // 기본 배송지가 없는 경우 새로운 기본 배송지를 추가
    public MemberAddress toEntity(Member member) {
        // MemberAddress의 설정
        BaseAddress baseAddress = this.toBaseAddress();

        return MemberAddress.builder()
                .member(member)
                .addressName("")
                .baseAddress(baseAddress)
                .phoneNumber(member.getPhoneNum())
                .emergencyPhoneNumber(null)
                .defaultAddressYn("Y") // 기본배송지(Y)
                .build();
    }

    public BaseAddress toBaseAddress() {
        return BaseAddress.builder()
                .zipCode(this.zipCode)
                .addressMain(this.addressMain)
                .addressDetail(this.addressDetail)
                .modificationDate(LocalDateTime.now())
                .build();
    }

}
