package shoppingmall.ankim.domain.member.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class MemberRegisterServiceRequest {

    private String id; // 아이디(이메일)
    private String pwd; // 비밀번호
    private String name; // 이름
    private String phoneNum; // 휴대전화번호
    private LocalDate birth; // 생년월일
    private String gender; // 성별 (남자 M, 여자 F)

    @Value("${member.default-grade}")
    private int defaultGrade;

    @Builder
    public MemberRegisterServiceRequest(String id, String pwd, String name, String phoneNum, LocalDate birth, String gender) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
    }

    // serviceRequest를 Member 엔티티로 변환해서 회원가입할 때 사용
    public Member create() {
        return Member.builder()
//                .uuid() // uuid 생성 로직 작성 후 값 넣기
                .id(this.id)
                .pwd(this.pwd)
                .name(this.name)
                .phoneNum(this.phoneNum)
                .gender(this.gender)
                .joinDate(LocalDateTime.now())
                .grade(defaultGrade) // 구입금액이 없기 때문에 grade번호 50을 default
                .status(MemberStatus.ACTIVE) // 가입하면 바로 활성상태이므로 ACTIVE가 default
                .build();
    }
}
