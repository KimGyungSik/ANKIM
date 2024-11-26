package shoppingmall.ankim.domain.item.repository.query;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.entity.QItem;
import shoppingmall.ankim.domain.itemOption.entity.QItemOption;

import java.util.List;

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

        return queryFactory
                .selectFrom(item) // 메인 : Item을 조회
                .where(
                        item.product.no.eq(productNo), // 조건 1 : 특정 상품 번호에 해당하는 품목-Item(해당 상품의 품목을 조회해야되기 때문)
                        item.no.in( // 조건 2 : 서브쿼리 결과에 포함된 품목-Item의 번호만 조회
                                JPAExpressions // 서브쿼리
                                        .select(itemOption.item.no) // 품목옵션-itemOption에서 품목-Item의 번호만 선택
                                        .from(itemOption) // 품목옵션-itemOption으로 부터 데이터를 조회
                                        /*
                                        * 서브쿼리 조건 1 : ItemOption이 특정 옵션 값 번호(optionValueNoList)들을 가지고 있어야 됨
                                        * 예 : 옵션 값 번호 리스트가 [1, 3]인 경우, ItemOption이 1과 3인 데이터를 조회
                                        * */
                                        .where(itemOption.optionValue.no.in(optionValueNoList)) //
                                        /*
                                        * 서브쿼리 그룹핑 : 품목-Item 단위로 데이터를 묶음
                                        * 같은 Item에 속한 옵션 값을 하나로 묶기 위해서 groupBy 사용
                                        * 예 : 품목옵션-ItemOption에서 품목-Item 1이 옵션 값 1, 3을 가지면 그룹화 결과에 Item 1이 포함
                                        * */
                                        .groupBy(itemOption.item.no)
                                        /*
                                        * 서브쿼리 조건 2 : 그룹핑한 데이터에서 옵션 값의 개수가 List로 넘긴 옵션의 개수와 일치해야 됨
                                        * - 예 : 옵션 값 리스트(optionValueNoList)가 [1, 3]인 경우,
                                        * - 해당 품목-Item이 정확하게 2개의 옵션 값을 가지고 있어야 조회를 해야됨
                                        * - 이 조건을 추가하지 않는다면? 알맞는 품목-Item을 조회 못할 수도 있음
                                        * */
                                        .having(itemOption.optionValue.no.count().eq((long) optionValueNoList.size()))
                        )
                )
                .fetchOne(); // 모든 조건에 맞는 품목-Item은 1개여야 되므로 단일 결과를 반환
    }
}
