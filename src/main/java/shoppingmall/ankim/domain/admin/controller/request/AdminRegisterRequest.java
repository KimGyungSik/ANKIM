package shoppingmall.ankim.domain.admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.service.request.AddressRegisterServiceRequest;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AdminRegisterRequest {

    @NotBlank(message = "올바른 아이디를 입력해주세요.")
    private String loginId; // 아이디

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$",
            message = "비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String pwd; // 비밀번호

    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z]{2,15}$",
            message = "이름은 공백, 숫자 없이 2~15자 이내로 입력해야 합니다."
    )
    private String name; // 이름

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "이메일을 입력해주세요."
    )
    private String email;

    @NotBlank(message = "휴대전화번호를 입력해주세요.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "휴대전화번호 형식이 올바르지 않습니다."
    )
    private String phoneNum; // 휴대전화번호(개인)

    private String officeNum; // 사무실유선전화

    @NotNull(message = "생년월일을 입력해주세요.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birth; // 생년월일

    @NotNull(message = "성별을 선택해주세요.")
    private String gender; // 성별 (남자 M, 여자 F)

    private String joinDate; // 입사일

    private AdminStatus status; // 관리자 상태

    @NotNull(message = "우편번호를 입력해주세요.")
    private Integer zipCode; // 우편번호
    @NotBlank(message = "주소를 선택해주세요.")
    private String addressMain; // 주소

    private String addressDetail; // 상세주소(필수기재는 아님)

    @Builder
    public AdminRegisterRequest(String loginId, String pwd, String name, String email, String phoneNum, String officeNum, LocalDate birth, String gender, String joinDate, AdminStatus status, Integer zipCode, String addressMain, String addressDetail) {
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
}
