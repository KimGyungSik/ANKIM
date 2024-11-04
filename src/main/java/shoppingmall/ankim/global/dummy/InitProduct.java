package shoppingmall.ankim.global.dummy;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.query.helper.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitProduct {
    private final InitProductService initProductService;

    @PostConstruct
    public void init() {
        initProductService.init();
    }
    @Component
    static class InitProductService {
        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            int productCount = 100;

            // 중분류 카테고리(최상위) 생성
            Map<Condition, Category> conditionToCategoryMap = new HashMap<>();
            for (Condition condition : Condition.values()) {
                if (condition.isCategoryCondition()) {
                    Category category = createCategory(em, condition.getCategoryName());
                    conditionToCategoryMap.put(condition, category);
                }
            }

            // 더미 데이터 생성
            for (int i = 0; i < productCount; i++) {
                // 동적 조건 설정
                Condition condition = getRandomCondition(i);
                Category category = conditionToCategoryMap.get(condition); // 해당 중분류 카테고리

                OrderBy orderBy = getRandomOrderBy(i);
                List<InfoSearch> infoSearches = getRandomInfoSearches(i);
                List<ColorCondition> colorConditions = getRandomColorConditions(i);
                PriceCondition priceCondition = getRandomPriceCondition(i);
                Integer customMinPrice = priceCondition == PriceCondition.CUSTOM ? 10000 : null;
                Integer customMaxPrice = priceCondition == PriceCondition.CUSTOM ? 50000 : null;

                // 상품 생성
                Product product = Product.builder()
                        .category(category)
                        .name("더미 상품 " + i)
                        .desc("테스트용 더미 상품입니다.")
                        .origPrice(10000 + (i * 1000)) // 기본 가격 설정
                        .discRate(condition == Condition.DISCOUNT ? 20 : 0) // 할인율 설정
                        .qty(infoSearches.contains(InfoSearch.EXCLUDE_OUT_OF_STOCK) ? 50 : 0) // 재고 설정
                        .rvwCnt(orderBy == OrderBy.HIGH_REVIEW ? 50 : 10) // 리뷰 수 설정
                        .viewCnt(orderBy == OrderBy.HIGH_VIEW ? 500 : 100) // 조회 수 설정
                        .wishCnt(condition == Condition.BEST ? 50 : 10) // 찜 수 설정
                        .freeShip(infoSearches.contains(InfoSearch.FREESHIP) ? "Y" : "N") // 무료 배송 여부
                        .handMadeYn(condition == Condition.HANDMADE ? "Y" : "N") // 핸드메이드 여부
                        .sellingStatus(ProductSellingStatus.SELLING)
                        .build();
                product.setCreatedAt(LocalDateTime.now().minusDays(i));
                em.persist(product);

                // 옵션 그룹 생성
                OptionGroup colorGroup = createOptionGroup(em, "컬러", product);
                OptionGroup sizeGroup = createOptionGroup(em, "사이즈", product);

                // 옵션 값 생성
                for (ColorCondition colorCondition : colorConditions) {
                    createOptionValue(em, colorCondition.name(), colorCondition.getHexCode(), colorGroup);
                }
                createOptionValue(em, "M", null, sizeGroup);
                createOptionValue(em, "L", null, sizeGroup);
                product.updateSearchKeywords();

                // 상품 이미지 생성
                addProductImages(em, product, i);

                // 품목 생성
                createItem(em, "색상: " + colorConditions.get(0).name() + ", 사이즈: M", List.of(
                        colorGroup.getOptionValues().get(0), // 첫 번째 색상
                        sizeGroup.getOptionValues().get(0)  // M
                ), product, i);

                createItem(em, "색상: " + colorConditions.get(0).name() + ", 사이즈: L", List.of(
                        colorGroup.getOptionValues().get(0), // 첫 번째 색상
                        sizeGroup.getOptionValues().get(1)  // L
                ), product, i);
            }

            System.out.println(productCount + "개의 상세한 조건을 가진 더미 상품이 생성되었습니다.");
        }

        // 카테고리 생성 메서드
        private Category createCategory(EntityManager em, String name) {
            Category category = Category.builder()
                    .name(name)
                    .build();
            em.persist(category);
            return category;
        }

        // 옵션 그룹 생성
        private OptionGroup createOptionGroup(EntityManager em, String name, Product product) {
            OptionGroup optionGroup = OptionGroup.builder()
                    .name(name)
                    .product(product)
                    .build();
            product.addOptionGroup(optionGroup);
            em.persist(optionGroup);
            return optionGroup;
        }

        // 옵션 값 생성
        private void createOptionValue(EntityManager em, String name, String colorCode, OptionGroup optionGroup) {
            OptionValue optionValue = OptionValue.builder()
                    .name(name)
                    .colorCode(colorCode)
                    .optionGroup(optionGroup)
                    .build();
            optionGroup.addOptionValue(optionValue);
            em.persist(optionValue);
        }

        // 상품 이미지 생성
        private void addProductImages(EntityManager em, Product product, int index) {
            ProductImg thumbnailImg = ProductImg.builder()
                    .imgName("thumbnail_" + index + ".jpg")
                    .oriImgName("썸네일 이미지 " + index)
                    .imgUrl("http://example.com/images/thumbnail_" + index + ".jpg")
                    .repimgYn("Y") // 대표 이미지
                    .ord(1)
                    .product(product)
                    .build();
            em.persist(thumbnailImg);

            ProductImg detailImg = ProductImg.builder()
                    .imgName("detail_" + index + ".jpg")
                    .oriImgName("상세 이미지 " + index)
                    .imgUrl("http://example.com/images/detail_" + index + ".jpg")
                    .repimgYn("N")
                    .ord(2)
                    .product(product)
                    .build();
            em.persist(detailImg);

            product.addProductImg(thumbnailImg);
            product.addProductImg(detailImg);
        }

        // 품목 생성
        private void createItem(EntityManager em, String name, List<OptionValue> optionValues, Product product, int index) {
            Item item = Item.builder()
                    .name(name)
                    .optionValues(optionValues)
                    .code("ITEM_" + index)
                    .addPrice(0)
                    .qty(50)
                    .safQty(10)
                    .maxQty(5)
                    .minQty(1)
                    .product(product)
                    .build();
            em.persist(item);
            product.addItem(item);
        }

        // 랜덤 Condition 생성
        private Condition getRandomCondition(int index) {
            return Condition.values()[index % Condition.values().length];
        }

        // 랜덤 OrderBy 생성
        private OrderBy getRandomOrderBy(int index) {
            return OrderBy.values()[index % OrderBy.values().length];
        }

        // 랜덤 InfoSearch 생성
        private List<InfoSearch> getRandomInfoSearches(int index) {
            return List.of(InfoSearch.values()[index % InfoSearch.values().length]);
        }

        // 랜덤 ColorCondition 생성
        private List<ColorCondition> getRandomColorConditions(int index) {
            return List.of(ColorCondition.values()[index % ColorCondition.values().length]);
        }

        // 랜덤 PriceCondition 생성
        private PriceCondition getRandomPriceCondition(int index) {
            return PriceCondition.values()[index % PriceCondition.values().length];
        }
    }

}