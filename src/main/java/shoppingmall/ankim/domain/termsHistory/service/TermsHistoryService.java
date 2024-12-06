package shoppingmall.ankim.domain.termsHistory.service;

import shoppingmall.ankim.domain.termsHistory.controller.request.TermsHistoryCreateRequest;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryShowResponse;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;

import java.util.List;

public interface TermsHistoryService {

    void updateTermsAgreement(TermsUpdateServiceRequest request);

}
