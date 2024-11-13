package shoppingmall.ankim.domain.terms.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.exception.TermsMandatoryNotAgreeException;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static shoppingmall.ankim.global.exception.ErrorCode.REQUIRED_TERMS_NOT_AGREED;

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
        List<Terms> allTerms = new ArrayList<>();

        for (TermsAgreement agreement : termsAgreements) {
            if (needsSubTerms(agreement)) {
                // 선택한 약관 추가 및 하위 약관이 존재한다면 하위 약관을 포함하여 추가
                allTerms.addAll(termsRepository.findSubTermsIncludingParent(agreement.getNo(), agreement.getLevel(), "Y"));
            }
        }
        return allTerms;
    }

    // 레벨2 약관이고 동의한 상태인지 확인
    private boolean needsSubTerms(TermsAgreement agreement) {
        return agreement.getAgreeYn().equals("Y") && agreement.getLevel().equals(2);
    }

    // 특정 부모 약관의 하위 레벨3 약관을 TermsAgreement 리스트로 반환
    private List<TermsAgreement> getSubTermsAsAgreements(Long parentNo, Integer level) {
        List<Terms> subTerms = termsRepository.findSubTermsForParent(parentNo, level, "Y");
        List<TermsAgreement> subAgreements = new ArrayList<>();
        for (Terms subTerm : subTerms) {
            subAgreements.add(TermsAgreement.builder()
                    .no(subTerm.getNo())
                    .name(subTerm.getName())
                    .agreeYn("Y")
                    .level(subTerm.getLevel())
                    .termsYn(subTerm.getTermsYn())
                    .build());
        }
        return subAgreements;
    }

}
