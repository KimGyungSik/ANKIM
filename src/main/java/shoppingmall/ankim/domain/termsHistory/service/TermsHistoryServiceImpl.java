package shoppingmall.ankim.domain.termsHistory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.exception.TermsNotFoundException;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsHistoryCreateRequest;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;

import java.util.ArrayList;
import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.TERMS_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TermsHistoryServiceImpl implements TermsHistoryService {

    private final TermsRepository termsRepository;
    private final TermsHistoryRepository termsHistoryRepository;


    /*
    * 1. 존재하는 약관인지 확인 (terms)
    * 2. 동의한 이력이 있는지 조회 (terms_history)
    * 3. 동의 이력이 없으면
    *    - terms_history에 insert
    * 4. 동의 이력이 있으면
    *    - 기존 이력을 업데이트 -> 비활성화 상태로
    *    - terms_history에 새롭게 동의한 내용 insert
    * 5. level3의 동의내역이 전부 동의인 경우에 level2인 상위 레벨도 동의한 것으로
    * 6. level3의 동의내역 하나라도 동의를 철회한 경우에는 level2인 상위 레벨도 동의 철회한 것으로
    * */
    @Override
    public void updateTermsAgreement(TermsUpdateServiceRequest request) {
        // 1. 존재하는 약관인지 확인
        Terms terms = termsRepository.findById(request.getTerms_no())
                .orElseThrow(() -> new TermsNotFoundException(TERMS_NOT_FOUND));

        // 2. 동의한 이력이 있는지 조회

    }
}
