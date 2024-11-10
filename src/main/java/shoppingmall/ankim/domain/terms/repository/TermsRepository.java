package shoppingmall.ankim.domain.terms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.query.TermsQueryRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long>, TermsQueryRepository {

    // 특정 부모 약관의 하위 약관 중 활성 상태가 Y인 약관 가져오기
    List<Terms> findByParentTermsNoAndActiveYn(Long parentNo, String activeYn);

    // 약관카테고리가 JOIN이면서 활성상태가 Y인 약관 불러오기
    List<Terms> findByCategoryAndActiveYn(TermsCategory category, String activeYn);


}
