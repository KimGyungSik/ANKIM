package shoppingmall.ankim.domain.terms.service;

import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryShowResponse;

import java.util.List;

public interface TermsService {

    List<Terms> join();
}
