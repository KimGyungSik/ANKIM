package shoppingmall.ankim.domain.item.repository.query;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.entity.QItem;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.itemOption.entity.QItemOption;
import shoppingmall.ankim.domain.product.entity.QProduct;

import java.util.List;
import java.util.Optional;

import static shoppingmall.ankim.global.exception.ErrorCode.ITEM_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    /*
     * 옵션 값 리스트와 상품 번호를 기반으로 Item 조회
     * 조건에 맞는 Item을 조회(한 개)
     * */
    @Override
    public Item findItemByOptionValuesAndProduct(Long productNo, List<Long> optionValueNoList) {
        // Q 클래스 선언
        QItem item = QItem.item;
        QItemOption itemOption = QItemOption.itemOption;
        QProduct product = QProduct.product;

        Item result =  queryFactory
                .selectFrom(item) // ITEM 테이블에서 조회
                .join(item.product, product).on(product.no.eq(productNo)) // PRODUCT 조인 및 조건 추가
                .join(item.itemOptions, itemOption) // ITEM_OPTION 조인
                .where(
                        itemOption.optionValue.no.in(optionValueNoList) // 옵션 값 번호 조건
                )
                .groupBy(item.no) // ITEM 단위로 그룹화
                .having(itemOption.optionValue.no.count().eq((long) optionValueNoList.size())) // HAVING 조건
                .fetchOne(); // 단일 결과 반환

        return Optional.ofNullable(result)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND));
    }

    /*
     * 상품 번호를 기반으로 Item 리스트 조회
     */
    public List<Item> findItemsByProductNo(Long productNo) {
        QItem item = QItem.item;
        QProduct product = QProduct.product;

        return queryFactory
                .selectFrom(item)
                .join(item.product, product)
                .where(product.no.eq(productNo)) // productNo 조건
                .fetch(); // 리스트로 반환
    }
}
