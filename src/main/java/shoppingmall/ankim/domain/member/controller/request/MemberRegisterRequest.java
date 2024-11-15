package shoppingmall.ankim.domain.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class MemberRegisterRequest {

    private String id; // 아이디(이메일)

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

    @NotBlank(message = "휴대전화번호를 입력해주세요.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "휴대전화번호 형식이 올바르지 않습니다."
    )
    private String phoneNum; // 휴대전화번호

    @NotNull(message = "생년월일을 입력해주세요.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
//    @Pattern(
//            regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
//            message = "생년월일 형식이 올바르지 않습니다."
//    )
    private LocalDate birth; // 생년월일

    @NotNull(message = "성별을 선택해주세요.")
    private String gender; // 성별 (남자 M, 여자 F)

    @Builder
    public MemberRegisterRequest(String id, String pwd, String name, String phoneNum, LocalDate birth, String gender) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
    }

    // Service단 Reqeust로 변경
    public MemberRegisterServiceRequest toServiceRequest() {
        return MemberRegisterServiceRequest.builder()
                .loginId(this.id)
                .pwd(this.pwd)
                .name(this.name)
                .phoneNum(this.phoneNum)
                .birth(this.birth)
                .gender(this.gender)
                .build();
    }

}
