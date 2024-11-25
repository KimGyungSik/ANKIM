package shoppingmall.ankim.domain.termsHistory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsHistoryCreateRequest;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TermsHistoryServiceImpl implements TermsHistoryService {

    private final TermsHistoryRepository termsHistoryRepository;

    public void createTermsHistory(List<TermsHistoryCreateServiceRequest> requests) {


//        termsHistoryRepository.saveAll(termsHistories);
    }

}
