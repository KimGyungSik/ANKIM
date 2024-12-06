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
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_MEMBER;
import static shoppingmall.ankim.global.exception.ErrorCode.TERMS_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TermsHistoryServiceImpl implements TermsHistoryService {

    private final TermsRepository termsRepository;
    private final TermsHistoryRepository termsHistoryRepository;
    private final MemberRepository memberRepository;


    /*
    * 1. 존재하는 약관인지 확인 (terms)
    * 2. 동의한 이력이 있는지 조회 (terms_history)
    * 3. 동의 이력이 없으면
    *    - terms_history에 insert
    * 4. 동의 이력이 있으면
    *    - 기존 이력을 업데이트 -> 비활성화 상태로
    *    - terms_history에 새롭게 동의한 내용 insert
    * 5. 현재 동의한 약관 레벨이 4인 경우
    *    - 4레벨의 동의내역이 전부 동의인 경우 3레벨도 동의한 것으로 변경
    *    - 4레벨의 동의내역 하나라도 동의를 철회한 경우 3레벨도 동의 철회한 것으로 변경
    * */
    @Override
    public void updateTermsAgreement(String loginId, List<TermsUpdateServiceRequest> requestList) {
        LocalDateTime now = LocalDateTime.now();
        Member member = getMember(loginId);

        for (TermsUpdateServiceRequest request : requestList) {
            // 1. 존재하는 약관인지 확인
            Terms terms = termsRepository.findById(request.getTerms_no())
                    .orElseThrow(() -> new TermsNotFoundException(TERMS_NOT_FOUND));

            // 2. 동의한 이력이 있는지 조회
            Optional<TermsHistory> existingHistory = termsHistoryRepository.findByMemberAndTerms(member.getNo(), request.getTerms_no());
            if (existingHistory.isEmpty()) { // 동의한 이력이 없는 경우
                TermsHistory newTermsHistory = request.toEntity(member, existingHistory.get().getTerms(), now);
            } else { // 동의한 이력이 있는 경우
                TermsHistory previousHistory = existingHistory.get();
                previousHistory.setActiveYn("N"); // 기존 이력을 비활성화

                // 새로운 이력을 추가
                TermsHistory newTermsHistory = request.toEntity(member, existingHistory.get().getTerms(), now);
            }

            // 현재 동의한 약관 레벨이 4인 경우
            Terms parentTerms = terms.getParentTerms();
            if (parentTerms == null) {
                return; // 상위 약관이 없으면 처리하지 않음
            }

            if (terms.getLevel() == 4 && request.getTerms_hist_agreeYn().equals("N")) {
                parentTerms.setActiveYn("N");
            } else if (terms.getLevel() == 4) { // 약관 레벨이 4이고 부모 약관이 같은 것들 모두 동의한 상태라면
                boolean allSubTermsAgreed = termsRepository.findAllSubTerms(parentTerms.getNo())
                        .stream()
                        .allMatch(subTerm -> termsHistoryRepository.isAgreed(member.getNo(), subTerm.getNo()));
                if (allSubTermsAgreed) {
                    parentTerms.setActiveYn("Y");
                }
            }
        }

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
