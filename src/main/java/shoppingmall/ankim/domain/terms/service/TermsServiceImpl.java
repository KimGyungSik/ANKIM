package shoppingmall.ankim.domain.terms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.terms.dto.TermsAgreeResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TermsServiceImpl implements TermsService {

    private final TermsRepository termsRepository;
    private final TermsHistoryRepository termsHistoryRepository;

    // 사용자의 이전 동의 내역과 최신 약관을 조합해서 반환
    @Override
    public List<TermsAgreeResponse> getTermsForMember(Long memberNo, TermsCategory category) {
        // 1. 사용자의 기존 동의 이력 조회 (category = JOIN, 선택 약관만)
        List<TermsHistory> previousHistories = termsHistoryRepository.findAgreedJoinByMember(memberNo, category)
                .stream()
                .filter(th -> "N".equals(th.getTerms().getTermsYn())) // 선택 약관만 필터링
                .toList();

        // 2. 최신 약관 조회 (category = JOIN, 선택 약관만)
        List<Terms> latestTerms = termsRepository.findLatestTerms(category, "Y")
                .stream()
                .filter(t -> "N".equals(t.getTermsYn())) // 선택 약관만 필터링
                .sorted((t1, t2) -> Integer.compare(t2.getTermsVersion(), t1.getTermsVersion())) // 최신 버전 순 정렬
                .toList();

        // 3. 기존 동의 내역을 Map으로 변환 (Key: 약관명, Value: 동의 내역)
        Map<String, TermsHistory> historyMap = previousHistories.stream()
                .collect(Collectors.toMap(th -> th.getTerms().getName(), th -> th));

        // 4. 최신 약관을 Map으로 변환 (Key: 약관명, Value: 최신 약관)
        Map<String, Terms> latestTermsMap = latestTerms.stream()
                .collect(Collectors.toMap(Terms::getName, t -> t));

        List<TermsAgreeResponse> responseList = new ArrayList<>();

        // 5. 기존 동의 내역과 최신 약관 비교
        for (Map.Entry<String, TermsHistory> entry : historyMap.entrySet()) {
            String termsName = entry.getKey();
            TermsHistory history = entry.getValue();
            Terms latest = latestTermsMap.get(termsName);

            if (latest != null) {
                responseList.add(TermsAgreeResponse.of(latest, "N")); // 최신 약관이므로 다시 동의 필요
                latestTermsMap.remove(termsName); // 최신 약관 리스트에서 제거 (중복 방지)
            } else {
                responseList.add(TermsAgreeResponse.of(history.getTerms(), history.getAgreeYn())); // 기존 약관 유지
            }
        }

        // 6. 최신 약관 리스트에 남아 있는 항목 추가 (새로운 약관)
        for (Terms latest : latestTermsMap.values()) {
            responseList.add(TermsAgreeResponse.of(latest, "N"));
        }

        return responseList;
    }

}
