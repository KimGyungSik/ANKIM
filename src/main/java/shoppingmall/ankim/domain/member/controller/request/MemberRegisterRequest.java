package shoppingmall.ankim.domain.member.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class MemberRegisterRequest {

    private String id; // 아이디(이메일)
    private String pwd; // 비밀번호
    private String name; // 이름
    private String phoneNum; // 휴대전화번호
    private LocalDate birth; // 생년월일
    private String gender; // 성별

}
