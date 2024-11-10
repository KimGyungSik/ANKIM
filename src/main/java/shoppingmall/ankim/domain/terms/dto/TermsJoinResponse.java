package shoppingmall.ankim.domain.terms.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TermsJoinResponse {

    Integer no; // 약관 번호
    String terms_yn; // 약관 필수 동의 여부(필수 Y, 선택 N)

    @Builder
    public TermsJoinResponse(Integer no, String terms_yn) {
        this.no = no;
        this.terms_yn = terms_yn;
    }
}
