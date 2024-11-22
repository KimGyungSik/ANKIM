package shoppingmall.ankim.domain.admin.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.admin.AdminAddress;
import shoppingmall.ankim.domain.address.service.request.AddressRegisterServiceRequest;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AdminRegisterServiceRequest {

    private String loginId; // 아이디
    private String pwd; // 비밀번호
    private String name; // 이름
    private String email;
    private String phoneNum; // 휴대전화번호(개인)
    private String officeNum; // 사무실유선전화
    private LocalDate birth; // 생년월일
    private String gender; // 성별 (남자 M, 여자 F)
    private String joinDate; // 입사일
    private AdminStatus status; // 관리자 상태
    private Integer zipCode; // 우편번호
    private String addressMain; // 주소
    private String addressDetail; // 상세주소(필수기재는 아님)

    @Builder
    public AdminRegisterServiceRequest(String loginId, String pwd, String name, String email, String phoneNum, String officeNum, LocalDate birth, String gender, String joinDate, AdminStatus status, Integer zipCode, String addressMain, String addressDetail) {
        this.loginId = loginId;
        this.pwd = pwd;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.officeNum = officeNum;
        this.birth = birth;
        this.gender = gender;
        this.joinDate = joinDate;
        this.status = status;
        this.zipCode = zipCode;
        this.addressMain = addressMain;
        this.addressDetail = addressDetail;
    }

    public AddressRegisterServiceRequest toServiceRequest() {
        return AddressRegisterServiceRequest.builder()
                .zipCode(this.zipCode)
                .addressMain(this.addressMain)
                .addressDetail(this.addressDetail)
                .build();
    }


    public Admin toAdminEntity(BaseAddress baseAddress) {
        Admin admin = Admin.builder()
                .loginId(this.loginId)
                .pwd(this.pwd)
                .name(this.name)
                .email(this.email)
                .phoneNum(this.phoneNum)
                .officeNum(this.officeNum)
                .birth(this.birth)
                .gender(this.gender)
                .joinDate(this.joinDate != null ? LocalDate.parse(this.joinDate) : LocalDate.now())
                .status(this.status != null ? this.status : AdminStatus.ACTIVE)
                .build();

        // AdminAddress 설정
        admin.registerAddress(baseAddress);
        return admin;
    }

    public BaseAddress toBaseAddress() {
        return BaseAddress.builder()
                .zipCode(this.zipCode)
                .addressMain(this.addressMain)
                .addressDetail(this.addressDetail)
                .build();
    }

}
