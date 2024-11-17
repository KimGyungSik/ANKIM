package shoppingmall.ankim.domain.member.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.terms.entity.Terms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberRegisterServiceRequest {

    private String loginId; // 아이디(이메일)
    private String pwd; // 비밀번호
    private String name; // 이름
    private String phoneNum; // 휴대전화번호
    private LocalDate birth; // 생년월일
    private String gender; // 성별 (남자 M, 여자 F)
    private Integer grade;
    private List<Terms> terms;

    @Builder
    public MemberRegisterServiceRequest(String loginId, String pwd, String name, String phoneNum, LocalDate birth, String gender, Integer grade, List<Terms> terms) {
        this.loginId = loginId;
        this.pwd = pwd;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
        this.grade = grade;
        this.terms = terms;
    }

    // serviceRequest를 Member 엔티티로 변환해서 회원가입할 때 사용
    public Member create(String encodePwd, List<Terms> terms) {
        return Member.builder()
//                .uuid() // uuid 생성 로직 작성 후 값 넣기
                .loginId(this.loginId)
                .pwd(encodePwd)
                .name(this.name)
                .phoneNum(this.phoneNum)
                .birth(this.birth)
                .gender(this.gender)
                .grade(this.grade) // 기본 등급 설정
                .status(MemberStatus.ACTIVE) // 가입하면 바로 활성상태이므로 ACTIVE가 default
                .termsList(terms)
                .build();
    }
}
