package shoppingmall.ankim.domain.terms.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.terms.entity.Terms;

// 회원가입 페이지에서 회원가입 약관을 보여주기 위해서 반환하는 데이터
// Entity -> TermsJoinResponse
@Data
@NoArgsConstructor
public class TermsJoinResponse {

    private Long no; // 약관번호
    private String name; // 약관명
    private String contents; // 약관 내용
    private String termsYn; // 필수동의 여부(필수 Y, 선택 N)
    private Integer level; // 약관 레벨

    @Builder
    public TermsJoinResponse(Long no, String name, String contents, String termsYn, Integer level) {
        this.no = no;
        this.name = name;
        this.contents = contents;
        this.termsYn = termsYn;
        this.level = level;
    }

    public static TermsJoinResponse of(Terms terms) {
        return TermsJoinResponse.builder()
                .no(terms.getNo())
                .name(terms.getName())
                .contents(terms.getContents())
                .termsYn(terms.getTermsYn())
                .level(terms.getLevel())
                .build();
    }
}
