package shoppingmall.ankim.domain.product.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgUrlResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.item.entity.QItem;
import shoppingmall.ankim.domain.itemOption.entity.QItemOption;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.dto.OptionValueResponse;
import shoppingmall.ankim.domain.option.entity.QOptionGroup;
import shoppingmall.ankim.domain.option.entity.QOptionValue;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;

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
    public ProductUserDetailResponse findProductUserDetailResponse(Long productId) {
        ProductUserDetailResponse result = findProductWithCategory(productId);

        result.setProductImgs(findProductImgUrlResponse(productId));
        result.setOptionGroups(findOptionGroupResponse(productId));

        return result;
    }

    @Override
    public ProductResponse adminDetailProduct(Long productId) {
        ProductResponse result = findProductAllWithCategory(productId);

        result.setProductImgs(findProductImgResponse(productId));
        result.setOptionGroups(findOptionGroupResponse(productId));
        result.setItems(findItemResponse(productId));

        return result;
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
