package shoppingmall.ankim.domain.product.repository.query;

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
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgUrlResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.dto.OptionValueResponse;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.QProduct;
import shoppingmall.ankim.domain.product.repository.query.helper.Condition;
import shoppingmall.ankim.domain.product.repository.query.helper.OrderBy;
import shoppingmall.ankim.domain.product.repository.query.helper.ProductQueryHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.category.entity.QCategory.category;
import static shoppingmall.ankim.domain.image.entity.QProductImg.*;
import static shoppingmall.ankim.domain.item.entity.QItem.*;
import static shoppingmall.ankim.domain.itemOption.entity.QItemOption.*;
import static shoppingmall.ankim.domain.option.entity.QOptionGroup.*;
import static shoppingmall.ankim.domain.option.entity.QOptionValue.*;
import static shoppingmall.ankim.domain.product.entity.QProduct.*;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public ProductUserDetailResponse findUserProductDetailResponse(Long productId) {
        ProductUserDetailResponse result = findProductWithCategory(productId);

        result.setProductImgs(findProductImgUrlResponse(productId));
        result.setOptionGroups(findOptionGroupResponse(productId));

        return result;
    }

    @Override
    public ProductResponse findAdminProductDetailResponse(Long productId) {
        ProductResponse result = findProductAllWithCategory(productId);

        result.setProductImgs(findProductImgResponse(productId));
        result.setOptionGroups(findOptionGroupResponse(productId));
        result.setItems(findItemResponse(productId));

        return result;
    }

    @Override
    public Page<ProductListResponse> findUserProductListResponse(Pageable pageable, Condition condition, OrderBy order, Long category, String keyword) {
        // 필터링
        BooleanBuilder filterBuilder = ProductQueryHelper.createFilterBuilder(condition, category, keyword, QProduct.product);

        // 정렬
        OrderSpecifier<?> orderSpecifier = ProductQueryHelper.getOrderSpecifier(order, product);

        // 필터링 및 정렬 적용
        List<ProductListResponse> content = getFilteredAndSortedResults(orderSpecifier, filterBuilder, pageable);

        // 전체 카운트 조회 쿼리
        JPAQuery<Product> countQuery = queryFactory.selectFrom(product)
                .where(filterBuilder);

        // PageableExecutionUtils.getPage()로 최적화
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    // 필터링 및 정렬 수행하는 메서드
    private List<ProductListResponse> getFilteredAndSortedResults(OrderSpecifier orderSpecifier, BooleanBuilder filterBuilder, Pageable pageable) {
        List<ProductListResponse> response = queryFactory
                .select(Projections.fields(ProductListResponse.class,
                        product.no,
                        category.name.as("categoryName"),
                        product.name,
                        product.code,
                        product.desc,
                        product.searchKeywords,
                        product.discRate,
                        product.sellPrice,
                        product.createdAt,
                        product.handMadeYn,
                        product.freeShip,
                        product.wishCnt,
                        product.rvwCnt,
                        product.viewCnt,
                        product.avgR
                ))
                .from(product)
                .leftJoin(product.category, category)
                .where(filterBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 상품 ID 리스트 추출
        List<Long> productIds = toProductListResponseIds(response);

        // 3. 상품 ID와 썸네일 URL 매핑
        Map<Long, String> thumbnailMap = getThumbnailUrls(productIds);

        // 4. 썸네일 URL을 ProductListResponse에 매핑
        response.forEach(product -> product.setThumbNailImgUrl(thumbnailMap.get(product.getNo())));

        return response;
    }

    private List<Long> toProductListResponseIds(List<ProductListResponse> responses) {
        return responses.stream()
                .map(ProductListResponse::getNo)
                .toList();
    }

    // 상품 ID에 해당하는 썸네일 URL 매핑
    private Map<Long, String> getThumbnailUrls(List<Long> productIds) {
        return queryFactory
                .select(productImg.product.no, productImg.imgUrl)
                .from(productImg)
                .where(productImg.product.no.in(productIds)
                        .and(productImg.repimgYn.eq("Y"))
                        .and(productImg.ord.eq(1)))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(productImg.product.no), // 상품 ID
                        tuple -> tuple.get(productImg.imgUrl)      // 썸네일 URL
                ));
    }


    private ProductResponse findProductAllWithCategory(Long productId) {
        ProductResponse response = queryFactory
                .select(Projections.fields(ProductResponse.class,
                        product.no,
                        product.name,
                        product.code,
                        product.desc,
                        product.discRate,
                        product.sellPrice,
                        product.origPrice,
                        product.optYn,
                        product.restockYn,
                        product.qty,
                        product.sellingStatus,
                        product.handMadeYn,
                        product.freeShip,
                        product.shipFee,
                        product.searchKeywords,
                        product.relProdCode,
                        product.cauProd,
                        product.cauOrd,
                        product.cauShip,
                        product.avgR,
                        product.wishCnt,
                        product.viewCnt,
                        product.rvwCnt,
                        product.qnaCnt,
                        product.dispOrd,
                        Projections.fields(CategoryResponse.class,
                                category.no.as("categoryNo"),
                                category.parent.no.as("parentNo"),
                                category.level,
                                category.name
                        ).as("categoryResponse")
                ))
                .from(product)
                .leftJoin(product.category, category)
                .where(product.no.eq(productId))
                .fetchOne();

        if (response != null && response.getCategoryResponse() != null) {
            List<CategoryResponse> childCategories = getChildCategories(response.getCategoryResponse().getCategoryNo());
            response.getCategoryResponse().setChildCategories(childCategories); // 자식 카테고리 설정
        }

        return response;
    }

    private ProductUserDetailResponse findProductWithCategory(Long productId) {
        ProductUserDetailResponse response = queryFactory
                .select(Projections.fields(ProductUserDetailResponse.class,
                        product.no,
                        product.name,
                        product.code,
                        product.desc,
                        product.discRate,
                        product.sellPrice,
                        product.origPrice,
                        product.sellingStatus,
                        product.handMadeYn,
                        product.freeShip,
                        product.shipFee,
                        product.searchKeywords,
                        product.relProdCode,
                        product.cauProd,
                        product.cauOrd,
                        product.cauShip,
                        product.avgR,
                        product.wishCnt,
                        product.viewCnt,
                        product.rvwCnt,
                        product.qnaCnt,
                        Projections.fields(CategoryResponse.class,
                                category.no.as("categoryNo"),
                                category.parent.no.as("parentNo"),
                                category.level,
                                category.name
                        ).as("categoryResponse")
                ))
                .from(product)
                .leftJoin(product.category, category)
                .where(product.no.eq(productId))
                .fetchOne();

        if (response != null && response.getCategoryResponse() != null) {
            List<CategoryResponse> childCategories = getChildCategories(response.getCategoryResponse().getCategoryNo());
            response.getCategoryResponse().setChildCategories(childCategories); // 자식 카테고리 설정
        }

        return response;
    }

    private List<CategoryResponse> getChildCategories(Long parentCategoryId) {
        return queryFactory
                .select(Projections.fields(CategoryResponse.class,
                        category.no.as("categoryNo"),
                        category.parent.no.as("parentNo"),
                        category.level,
                        category.name
                ))
                .from(category)
                .where(category.parent.no.eq(parentCategoryId))
                .fetch();
    }

    private List<ProductImgUrlResponse> findProductImgUrlResponse(Long productId) {
        return queryFactory
                .select(Projections.fields(ProductImgUrlResponse.class,
                        productImg.imgUrl,
                        productImg.repimgYn.as("repImgYn"),
                        productImg.ord
                ))
                .from(productImg)
                .where(productImg.product.no.eq(productId))
                .fetch();
    }

    private List<ProductImgResponse> findProductImgResponse(Long productId) {
        return queryFactory
                .select(Projections.fields(ProductImgResponse.class,
                        productImg.no.as("id"),
                        productImg.imgName,
                        productImg.oriImgName,
                        productImg.imgUrl,
                        productImg.repimgYn.as("repImgYn"),
                        productImg.ord
                ))
                .from(productImg)
                .where(productImg.product.no.eq(productId))
                .fetch();
    }


    private List<OptionGroupResponse> findOptionGroupResponse(Long productId) {
        List<OptionGroupResponse> responses = queryFactory
                .select(Projections.fields(OptionGroupResponse.class,
                        optionGroup.no.as("optionGroupNo"),
                        optionGroup.name.as("groupName")
                ))
                .from(optionGroup)
                .where(optionGroup.product.no.eq(productId))
                .fetch();

        List<Long> optionGroupIds = toOptionGroupIds(responses);

        Map<Long, List<OptionValueResponse>> optionValueMap = findOptionValueResponse(optionGroupIds).stream()
                .collect(Collectors.groupingBy(OptionValueResponse::getOptionGroupNo));

        responses.forEach(o -> o.setOptionValueResponses(optionValueMap.get(o.getOptionGroupNo())));

        return responses;
    }

    private List<Long> toOptionGroupIds(List<OptionGroupResponse> responses) {
        return responses.stream()
                        .map(OptionGroupResponse::getOptionGroupNo)
                        .toList();
    }

    private List<OptionValueResponse> findOptionValueResponse(List<Long> optionGroupId) {
        return queryFactory
                .select(Projections.fields(OptionValueResponse.class,
                        optionValue.no.as("optionValueNo"),
                        optionValue.name.as("valueName"),
                        optionValue.colorCode.as("colorCode"),
                        optionValue.optionGroup.no.as("optionGroupNo")
                ))
                .from(optionValue)
                .where(optionValue.optionGroup.no.in(optionGroupId))
                .fetch();
    }

    private List<ItemResponse> findItemResponse(Long productId) {
        // ItemResponse 리스트 조회
        List<ItemResponse> response = queryFactory
                .select(Projections.fields(ItemResponse.class,
                        item.no.as("itemId"),
                        item.code,
                        item.name,
                        item.addPrice,
                        item.qty,
                        item.safQty,
                        item.sellingStatus,
                        item.maxQty,
                        item.minQty
                ))
                .from(item)
                .where(item.product.no.eq(productId))
                .fetch();

        // Item ID 리스트 추출
        List<Long> itemIds = response.stream()
                .map(ItemResponse::getItemId)
                .toList();

        // Item-OptionValue 관계 조회
        Map<Long, List<OptionValueResponse>> itemOptionValueMap = findItemOptionValueResponses(itemIds);

        // 각 ItemResponse에 OptionValueResponse 리스트 설정
        response.forEach(item -> item.setOptionValues(itemOptionValueMap.get(item.getItemId())));

        return response;
    }

    private Map<Long, List<OptionValueResponse>> findItemOptionValueResponses(List<Long> itemIds) {
        return queryFactory
                .select(Projections.fields(OptionValueResponse.class,
                        optionValue.no.as("optionValueNo"),
                        optionValue.name.as("valueName"),
                        optionValue.colorCode.as("colorCode"),
                        itemOption.item.no.as("itemId") // Item과 매핑을 위한 필드
                ))
                .from(itemOption)
                .join(itemOption.optionValue, optionValue)
                .where(itemOption.item.no.in(itemIds)) // Item ID 리스트 사용
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        OptionValueResponse::getItemId // Item ID를 기준으로 그룹화
                ));
    }

}
