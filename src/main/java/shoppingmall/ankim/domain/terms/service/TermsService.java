package shoppingmall.ankim.domain.terms.service;

import shoppingmall.ankim.domain.terms.dto.TermsAgreeResponse;
import shoppingmall.ankim.domain.terms.dto.TermsLeaveResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;

import java.util.List;

public interface TermsService {
    List<TermsAgreeResponse> getTermsForMember(Long memberNo, TermsCategory category);
}
