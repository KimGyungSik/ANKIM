package shoppingmall.ankim.domain.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.dto.TermsAgreeResponse;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MemberInfoResponse {
    /*
    * [로그인 정보]
    * 회원번호(pk)
    * 아이디(이메일)
    * 비밀번호 -> 이 정보는 넘겨주지 않아도 됨
    * */
    private Long no;
    private String loginId;
    private String password;

    /*
    * [회원 정보]
    * 이름
    * 연락처
    * 이메일
    * 주소정보
    * */
    private String name;
    private String phoneNum;
    private LocalDate birth;

    private MemberAddressResponse address;

    /*
    * [마케팅 및 광고 알림 설정]
    * 약관 이력
    * */
    private List<TermsAgreeResponse> agreedTerms;

    @Builder
    public MemberInfoResponse(Long no, String loginId, String password, String name, String phoneNum, LocalDate birth, MemberAddressResponse address, List<TermsAgreeResponse> agreedTerms) {
        this.no = no;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.address = address;
        this.agreedTerms = agreedTerms;
    }

    public static MemberInfoResponse of(Member member, MemberAddressResponse address, List<TermsAgreeResponse> agreedTerms) {
        return MemberInfoResponse.builder()
                .no(member.getNo())
                .loginId(member.getLoginId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .birth(member.getBirth())
                .address(address)
                .agreedTerms(agreedTerms)
                .build();
    }

}
