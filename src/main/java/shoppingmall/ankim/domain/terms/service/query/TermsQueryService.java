package shoppingmall.ankim.domain.terms.service.query;

import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.List;

public interface TermsQueryService {

    // 회원가입 약관을 전달한다.
    List<TermsJoinResponse> findJoinTerm();

    // 회원가입 필수 약관을 동의했는지 하지 않았는지 확인하고, 하위 레벨이 있는데 동의한 경우 하위 레벨도 동의한다.
    List<Terms> validateAndAddSubTerms(List<TermsAgreement> termsAgreement);
}
