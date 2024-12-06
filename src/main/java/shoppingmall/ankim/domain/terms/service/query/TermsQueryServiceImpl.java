package shoppingmall.ankim.domain.terms.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.exception.TermsMandatoryNotAgreeException;
import shoppingmall.ankim.domain.terms.exception.TermsNotFoundException;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static shoppingmall.ankim.global.exception.ErrorCode.REQUIRED_TERMS_NOT_AGREED;
import static shoppingmall.ankim.global.exception.ErrorCode.TERMS_NOT_FOUND;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TermsQueryServiceImpl implements TermsQueryService {

    private final TermsRepository termsRepository;

    // 회원가입 약관을 전달한다.(회원가입-약관동의 페이지)
    @Override
    public List<TermsJoinResponse> findJoinTerm() {
        List<TermsJoinResponse> responses = termsRepository.findLevelSubTerms(TermsCategory.JOIN, 2, "Y");
        if(responses.isEmpty()) {
            responses = new ArrayList<>();
        }
        return responses;
    }

    // 회원가입 필수 약관을 동의했는지 하지 않았는지 확인한다.
    @Override
    public List<Terms> validateAndAddSubTerms(List<TermsAgreement> termsAgreements) {
//        System.out.println("회원가입 필수 약관을 동의했는지 하지 않았는지 확인한다.");
//        System.out.println("동의한거 값 확인 : " + termsAgreements.size());
        // 필수 약관 동의 했는지 검증
        validateTerms(termsAgreements);
        // 하위 레벨이 있는 약관을 동의했는지 확인하고 리스트에 추가
        return addSubTermsIfNeeded(termsAgreements);
    }

    // 필수 약관 동의 했는지 검증
    public void validateTerms(List<TermsAgreement> termsAgreements) {
        for (TermsAgreement aggrement : termsAgreements) {
            if(aggrement.getTermsYn().equals("Y") && !aggrement.getAgreeYn().equals("Y")) {
                throw new TermsMandatoryNotAgreeException(REQUIRED_TERMS_NOT_AGREED);
            }
        }
    }

    // Terms로 변환하여 하위 약관 추가
    private List<Terms> addSubTermsIfNeeded(List<TermsAgreement> termsAgreements) {
        // 중복 제거를 위해 Set 사용
        Set<Terms> allTermsSet = new HashSet<>();

        for (TermsAgreement agreement : termsAgreements) {
            if (agreement.getAgreeYn().equals("Y")) {
                // 현재 동의한 약관 추가
                Terms currentTerms = termsRepository.findById(agreement.getNo())
                        .orElseThrow(() -> new TermsNotFoundException(TERMS_NOT_FOUND));
                allTermsSet.add(currentTerms);

                // 하위 약관 탐색 및 추가
                addAllSubTermsRecursively(currentTerms, allTermsSet);
            }
        }

        // Set을 List로 변환하여 반환
        return new ArrayList<>(allTermsSet);
    }

    // 재귀적으로 하위 약관 탐색 및 추가
    private void addAllSubTermsRecursively(Terms parentTerms, Set<Terms> allTermsSet) {
        List<Terms> subTerms = termsRepository.findAllSubTermsIncludingParent(parentTerms.getNo(), "Y");

        for (Terms subTerm : subTerms) {
            // 중복 방지를 위해 Set에 추가
            if (allTermsSet.add(subTerm)) {
                // 새로 추가된 약관에 대해 하위 약관 탐색
                addAllSubTermsRecursively(subTerm, allTermsSet);
            }
        }
    }

    // 레벨2이거나 레벨3 약관이고 동의한 상태인지 확인
    private boolean needsSubTerms(TermsAgreement agreement) {
        return agreement.getAgreeYn().equals("Y") &&
                (agreement.getLevel().equals(2) || agreement.getLevel().equals(3));
    }

}
