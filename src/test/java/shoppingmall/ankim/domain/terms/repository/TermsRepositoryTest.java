package shoppingmall.ankim.domain.terms.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class TermsRepositoryTest {

    @Autowired
    private TermsRepository termsRepository;

    @Test
    @DisplayName("상위 약관을 통해 모든 하위 약관을 재귀적으로 조회한다.")
    void findAllSubTermsRecursively() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String activeYn = "Y";

        // 상위 약관 조회 (e.g., "회원가입 약관")
        List<Terms> topLevelTerms = termsRepository.findByCategoryAndActiveYn(category, activeYn);

        // when
        Terms mainTerms = topLevelTerms.stream()
                .filter(terms -> terms.getParentTerms() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상위 약관이 존재하지 않습니다."));

        // 모든 하위 약관을 재귀적으로 조회
        List<Terms> allSubTerms = getAllSubTerms(mainTerms);

        // then
        assertNotNull(mainTerms);
        assertEquals("회원가입 약관", mainTerms.getName());
        assertEquals(7, allSubTerms.size()); // 회원가입 약관이 총 7개 있는지 확인
    }

    private List<Terms> getAllSubTerms(Terms term) {
        List<Terms> result = new ArrayList<>();
        result.add(term);

        // 복사본을 사용하여 ConcurrentModificationException 방지
        List<Terms> subTermsCopy = new ArrayList<>(term.getSubTerms());
        for (Terms subTerm : subTermsCopy) {
            result.addAll(getAllSubTerms(subTerm));
        }

        return result;
    }
}