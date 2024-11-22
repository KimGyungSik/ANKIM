package shoppingmall.ankim.domain.product.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgUrlResponse;
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
                .select( Projections.fields(OptionValueResponse.class,
                        optionValue.no.as("optionValueNo"),
                        optionValue.name.as("valueName"),
                        optionValue.colorCode.as("colorCode"),
                        optionValue.optionGroup.no.as("optionGroupNo")
                ))
                .from(optionValue)
                .where(optionValue.optionGroup.no.in(optionGroupId))
                .fetch();
    }



    @Override
    public ProductResponse adminDetailProduct(Long productId) {
        return null;
    }
}
