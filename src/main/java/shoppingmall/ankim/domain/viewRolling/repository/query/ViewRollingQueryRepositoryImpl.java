package shoppingmall.ankim.domain.viewRolling.repository.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.category.entity.QCategory;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.entity.QProduct;
import shoppingmall.ankim.domain.product.repository.query.ProductQueryRepositoryImpl;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;
import shoppingmall.ankim.domain.viewRolling.entity.ViewRolling;

import java.util.List;
import java.util.Map;
import static shoppingmall.ankim.domain.viewRolling.entity.QViewRolling.viewRolling;

@Repository
@RequiredArgsConstructor
public class ViewRollingQueryRepositoryImpl implements ViewRollingQueryRepository{
    private final JPAQueryFactory queryFactory;
    private final ProductQueryRepositoryImpl productQueryRepository;
    @Override
    public Page<ProductListResponse> getViewRollingProducts(
            Long categoryNo, RollingPeriod period, Pageable pageable) {

        BooleanBuilder filterBuilder = new BooleanBuilder();
        if (categoryNo != null) {
            filterBuilder.and(viewRolling.category.no.eq(categoryNo));
        }
        if (period != null) {
            filterBuilder.and(viewRolling.period.eq(period));
        }

        OrderSpecifier<?> orderSpecifier = viewRolling.totalViews.desc();

        List<ProductListResponse> response = queryFactory
                .select(Projections.fields(ProductListResponse.class,
                        QProduct.product.no,
                        QCategory.category.name.as("categoryName"),
                        QProduct.product.name,
                        QProduct.product.code,
                        QProduct.product.desc,
                        QProduct.product.qty,
                        QProduct.product.searchKeywords,
                        QProduct.product.discRate,
                        QProduct.product.sellPrice,
                        QProduct.product.createdAt,
                        QProduct.product.handMadeYn,
                        QProduct.product.freeShip,
                        QProduct.product.wishCnt,
                        QProduct.product.rvwCnt,
                        QProduct.product.viewCnt,
                        QProduct.product.avgR
                ))
                .from(viewRolling)
                .leftJoin(viewRolling.product, QProduct.product)
                .leftJoin(viewRolling.category, QCategory.category)
                .where(filterBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> productIds = productQueryRepository.toProductListResponseIds(response);

        Map<Long, String> thumbnailMap = productQueryRepository.getThumbnailUrls(productIds);
        response.forEach(product -> product.setThumbNailImgUrl(thumbnailMap.get(product.getNo())));

        // 전체 카운트 조회 쿼리
        JPAQuery<ViewRolling> countQuery = queryFactory.selectFrom(viewRolling)
                .where(filterBuilder);

        // PageableExecutionUtils.getPage()로 최적화
        return PageableExecutionUtils.getPage(response, pageable, countQuery::fetchCount);
    }

}
