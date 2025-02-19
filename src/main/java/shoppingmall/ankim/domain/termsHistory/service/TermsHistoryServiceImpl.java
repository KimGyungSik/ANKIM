package shoppingmall.ankim.domain.termsHistory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.exception.TermsNotFoundException;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsHistoryCreateRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsUpdateRequest;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryUpdateResponse;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.exception.EmptyTermsUpdateRequestException;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;

import java.time.LocalDateTime;
import java.util.*;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TermsHistoryServiceImpl implements TermsHistoryService {

    private final TermsRepository termsRepository;
    private final TermsHistoryRepository termsHistoryRepository;
    private final MemberRepository memberRepository;

    /*
    * 광고성 정보 수신을 동의하는 경우 마케팅 목적의 개인정보 수집 및 이용 동의를 필수로 진행해야 한다.
    * 마케팅 목적의 개인정보 수집 및 이용 동의는 단독으로 진행할 수 있다.
    * 마케팅 목적의 개인정보 수집 및 이용동의를 철회할 경우, 광고성 정보 수신 동의도 철회해야 한다.
    * 문자메시지, 이메일은 단독으로 취소할 수 있다.
    * */

    /*
    * 1. 존재하는 약관인지 확인 (terms)
    * 2. 동의한 이력이 있는지 조회 (terms_history)
    * 3. 동의 이력이 없으면
    *    - terms_history에 insert
    * 4. 동의 이력이 있으면
    *    - 기존 이력을 업데이트 -> 비활성화 상태로
    *    - terms_history에 새롭게 동의한 내용 insert
    * 5. 현재 동의한 약관 레벨이 3인 경우
    *    - 3레벨의 동의내역이 전부 동의인 경우 2레벨도 동의한 것으로 변경
    *    - 3레벨의 동의내역 하나라도 동의를 철회한 경우 2레벨도 동의 철회한 것으로 변경
    * */
    @Override
    public TermsHistoryUpdateResponse updateTermsAgreement(String loginId, List<TermsUpdateServiceRequest> requestList) {
        LocalDateTime now = LocalDateTime.now();
        Member member = getMember(loginId);

        // 약관 이름 수집을 위한 리스트
        List<String> termsNames = new ArrayList<>();

        // 부모 약관 처리 결과 저장용 Map -> 동일한 상위 약관을 삽입하지 않기 위해 key로 관리
        Map<Long, TermsHistory> parentTermsHistoryMap = new HashMap<>();

        for (TermsUpdateServiceRequest request : requestList) {
            // 1. 존재하는 약관인지 확인
            Terms terms = termsRepository.findById(request.getTerms_no())
                    .orElseThrow(() -> new TermsNotFoundException(TERMS_NOT_FOUND));
            String message = request.getTerms_hist_agreeYn().equals("Y") ? " 동의가 완료되었습니다." : " 거부가 완료되었습니다.";
            termsNames.add(terms.getName() + message);
            // 2. 동의한 이력이 있는지 조회
            Optional<TermsHistory> existingHistory = termsHistoryRepository.findByMemberAndTerms(member.getNo(), request.getTerms_no());
            TermsHistory newTermsHistory;
            if (existingHistory.isEmpty()) { // 동의한 이력이 없는 경우
                newTermsHistory = request.toEntity(member, terms, now); // 이력이 없기 때문에 getTerms가 없으므로 조회한 약관으로 수행
            } else { // 동의한 이력이 있는 경우
                TermsHistory previousHistory = existingHistory.get();
                previousHistory.setActiveYn("N"); // 기존 이력을 비활성화

                // 새로운 이력을 추가
                newTermsHistory = request.toEntity(member, existingHistory.get().getTerms(), now);
            }
            termsHistoryRepository.save(newTermsHistory);


            // 상위 약관이 존재하는지 확인
            Terms parentTerms = terms.getParentTerms();
            if (parentTerms == null || parentTermsHistoryMap.containsKey(parentTerms.getNo())) {
                continue; // 부모 약관이 없거나 이미 처리된 경우 다음 약관 처리로 이동
            }
            // 상위 약관동의 이력이 존재하는지 확인
            Optional<TermsHistory> parentExistingHistory = termsHistoryRepository.findByMemberAndTerms(member.getNo(), parentTerms.getNo());
            if(parentExistingHistory != null && parentExistingHistory.isPresent()) {
                parentExistingHistory.get().setActiveYn("N");
            }
            TermsHistory parentHistory;

            // 현재 동의한 약관 레벨이 3인 경우
            if (terms.getLevel() == 3 && request.getTerms_hist_agreeYn().equals("N")) { // 약관레벨이 3이고 약관동의를 철회한 경우
                parentHistory = request.toEntity(member, parentTerms, now);
                termsHistoryRepository.save(parentHistory);
            } else if (terms.getLevel() == 3) { // 약관 레벨이 3이고 부모 약관이 같은 것들 모두 동의한 상태라면(광고성 동의)
                boolean allSubTermsAgreed = termsRepository.findAllSubTerms(parentTerms.getNo())
                        .stream()
                        .allMatch(subTerm -> termsHistoryRepository.isAgreed(member.getNo(), subTerm.getNo()));
                if (allSubTermsAgreed) { // 하위 약관이 모두 동의된 것이 확인 된다면
                    parentHistory = request.toEntity(member, parentTerms, now); // 약관이력을 만들고
                    parentTermsHistoryMap.put(parentTerms.getNo(), parentHistory);
                }
            }
        }

        // 부모 약관 처리 결과 저장
        termsHistoryRepository.saveAll(parentTermsHistoryMap.values());
        return TermsHistoryUpdateResponse.of(termsNames, now);
    }

    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            log.error("회원 정보가 존재하지 않습니다.");
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }
}
