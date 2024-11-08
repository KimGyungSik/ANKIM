package shoppingmall.ankim.domain.member.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class MemberRegisterServiceRequest {

    private String id; // 아이디(이메일)
    private String pwd; // 비밀번호
    private String name; // 이름
    private String phoneNum; // 휴대전화번호
    private LocalDate birth; // 생년월일
    private String gender; // 성별 (남자 M, 여자 F)

    @Builder
    public MemberRegisterServiceRequest(String id, String pwd, String name, String phoneNum, LocalDate birth, String gender) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
    }
}
