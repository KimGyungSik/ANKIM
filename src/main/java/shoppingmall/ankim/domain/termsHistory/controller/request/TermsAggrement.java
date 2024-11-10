package shoppingmall.ankim.domain.termsHistory.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TermsAggrement {

    private Integer termNo; // 약관 번호
    private boolean aggred; // 약관 동의 여부

}
