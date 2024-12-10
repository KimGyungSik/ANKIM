package shoppingmall.ankim.domain.termsHistory.service;

import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryUpdateResponse;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;

import java.util.List;

public interface TermsHistoryService {

    TermsHistoryUpdateResponse updateTermsAgreement(String loginId, List<TermsUpdateServiceRequest> requestList);

}
