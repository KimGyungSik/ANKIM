package shoppingmall.ankim.domain.terms.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.terms.entity.Terms;

@Data
@NoArgsConstructor
public class TermsLeaveResponse {
    private Long no;
    private String name; // 약관명
    private String contents; // 약관 내용

    @Builder
    public TermsLeaveResponse(Long no, String name, String contents) {
        this.no = no;
        this.name = name;
        this.contents = contents;
    }

    public static TermsLeaveResponse of(Terms terms) {
        return TermsLeaveResponse.builder()
                .no(terms.getNo())
                .name(terms.getName())
                .contents(terms.getContents())
                .build();
    }
}
