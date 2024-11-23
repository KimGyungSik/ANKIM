package shoppingmall.ankim.domain.product.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.image.dto.ProductImgResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgUrlResponse;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.query.helper.Condition;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class ProductRepositoryTest {
    @MockBean
    private S3Service s3Service;

    @Autowired
    EntityManager em;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void setup() {
        // 중분류 -> 소분류 구조 정의
        Map<String, List<String>> categoryStructure = new HashMap<>();
        categoryStructure.put("Top", List.of("티셔츠", "맨투맨"));
        categoryStructure.put("Outer", List.of("자켓", "코트"));
        categoryStructure.put("Knit", List.of("스웨터", "가디건"));

        // 테스트 데이터 생성
        ProductFactory.createTestProductsWithSubcategories(em, 10, 10, 10, 10, categoryStructure);
    }


    @DisplayName("유저를 위한 상세 상품 페이지 단건 조회가 가능하다.")
    @Test
    void findProductUserDetailResponse() {
        // given
        Product product = ProductFactory.createProduct(em);

        // when
        ProductUserDetailResponse result = productRepository.findUserProductDetailResponse(product.getNo());

        // then
        assertThat(result).isNotNull();

        // 기본 필드 검증
        assertThat(result.getNo()).isEqualTo(product.getNo());
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getCode()).isEqualTo(product.getCode());
        assertThat(result.getDesc()).isEqualTo(product.getDesc());
        assertThat(result.getDiscRate()).isEqualTo(product.getDiscRate());
        assertThat(result.getSellPrice()).isEqualTo(product.getSellPrice());
        assertThat(result.getOrigPrice()).isEqualTo(product.getOrigPrice());
        assertThat(result.getSellingStatus()).isEqualTo(product.getSellingStatus());
        assertThat(result.getSearchKeywords()).isEqualTo(product.getSearchKeywords());

        // 카테고리 검증
        assertThat(result.getCategoryResponse()).isNotNull();
        assertThat(result.getCategoryResponse().getCategoryNo()).isEqualTo(product.getCategory().getNo());
        assertThat(result.getCategoryResponse().getName()).isEqualTo(product.getCategory().getName());

        // 상품 이미지 검증
        List<ProductImgUrlResponse> productImgs = result.getProductImgs();
        assertThat(productImgs).isNotNull();
        assertThat(productImgs).hasSize(2); // 이미지 개수 확인
        assertThat(productImgs.get(0).getRepImgYn()).isEqualTo("Y"); // 대표 이미지 여부 확인

        // 옵션 그룹 검증
        List<OptionGroupResponse> optionGroups = result.getOptionGroups();
        assertThat(optionGroups).isNotNull();
        assertThat(optionGroups).hasSize(2); // 옵션 그룹 수 확인
        assertThat(optionGroups.get(0).getGroupName()).isEqualTo("컬러"); // 첫 번째 옵션 그룹 이름 확인

        // 옵션 값 검증
        assertThat(optionGroups.get(0).getOptionValueResponses()).hasSize(2); // 첫 번째 옵션 그룹의 옵션 값 개수 확인
        assertThat(optionGroups.get(0).getOptionValueResponses().get(0).getValueName()).isEqualTo("블랙"); // 첫 번째 옵션 값 이름 확인
    }

    @DisplayName("관리자를 위한 상세 상품 페이지 단건 조회가 가능하다.")
    @Test
    void adminDetailProduct() {
        // given
        Product product = ProductFactory.createProduct(em);

        // when
        ProductResponse result = productRepository.findAdminProductDetailResponse(product.getNo());

        // then
        assertThat(result).isNotNull();

        // 기본 필드 검증
        assertThat(result.getNo()).isEqualTo(product.getNo());
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getCode()).isEqualTo(product.getCode());
        assertThat(result.getDesc()).isEqualTo(product.getDesc());
        assertThat(result.getDiscRate()).isEqualTo(product.getDiscRate());
        assertThat(result.getSellPrice()).isEqualTo(product.getSellPrice());
        assertThat(result.getOrigPrice()).isEqualTo(product.getOrigPrice());
        assertThat(result.getSellingStatus()).isEqualTo(product.getSellingStatus());
        assertThat(result.getSearchKeywords()).isEqualTo(product.getSearchKeywords());

        // 카테고리 검증
        assertThat(result.getCategoryResponse()).isNotNull();
        assertThat(result.getCategoryResponse().getCategoryNo()).isEqualTo(product.getCategory().getNo());
        assertThat(result.getCategoryResponse().getName()).isEqualTo(product.getCategory().getName());

        // 상품 이미지 검증
        List<ProductImgResponse> productImgs = result.getProductImgs();
        assertThat(productImgs).isNotNull();
        assertThat(productImgs).hasSize(2); // 이미지 개수 확인
        assertThat(productImgs.get(0).getRepImgYn()).isEqualTo("Y"); // 대표 이미지 여부 확인

        // 옵션 그룹 검증
        List<OptionGroupResponse> optionGroups = result.getOptionGroups();
        assertThat(optionGroups).isNotNull();
        assertThat(optionGroups).hasSize(2); // 옵션 그룹 수 확인
        assertThat(optionGroups.get(0).getGroupName()).isEqualTo("컬러"); // 첫 번째 옵션 그룹 이름 확인

        // 옵션 값 검증
        assertThat(optionGroups.get(0).getOptionValueResponses()).hasSize(2); // 첫 번째 옵션 그룹의 옵션 값 개수 확인
        assertThat(optionGroups.get(0).getOptionValueResponses().get(0).getValueName()).isEqualTo("블랙"); // 첫 번째 옵션 값 이름 확인

        // 품목 검증
        List<ItemResponse> items = result.getItems();
        assertThat(items.get(0).getName()).isEqualTo("색상: 블랙, 사이즈: M");
        assertThat(items.get(1).getName()).isEqualTo("색상: 블랙, 사이즈: L");
        assertThat(items.get(0).getQty()).isEqualTo(50);
        assertThat(items.get(1).getQty()).isEqualTo(30);
        assertThat(items.get(0).getOptionValues()).isNotEmpty();
        assertThat(items.get(1).getOptionValues()).isNotEmpty();
    }

    @DisplayName("홈페이지 메뉴바에서 NEW라는 메뉴를 클릭하면 최신 한달 이내 등록 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithConditionOfNew() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Condition condition = Condition.NEW;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .allMatch(product -> product.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusMonths(1)));
    }

    @DisplayName("홈페이지 메뉴바에서 BEST라는 메뉴를 클릭하면 찜횟수가 30개 이상인 베스트 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithConditionOfBEST() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Condition condition = Condition.BEST;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .allMatch(product -> product.getWishCnt() >= 30);
    }

    @DisplayName("홈페이지 메뉴바에서 HANDMADE라는 메뉴를 클릭하면 핸드메이드 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithConditionOfHANDMADE() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Condition condition = Condition.HANDMADE;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .allMatch(product -> product.getHandMadeYn().equals("Y"));
    }

    @DisplayName("홈페이지 메뉴바에서 DISCOUNT라는 메뉴를 클릭하면 할인중인 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithConditionOfDISCOUNT() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Condition condition = Condition.DISCOUNT;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .allMatch(product -> product.getDiscRate() > 0);
    }

    @DisplayName("홈페이지 메뉴바에서 원하는 중분류 카테고리 메뉴를 클릭하면 중분류에 해당하는 모든 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithConditionOfMiddleCategory() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Condition condition = Condition.TOP;

        // 중분류 카테고리 이름 설정 (예: "상의"가 중분류)
        String middleCategoryName = "Top";

        // 중분류 카테고리에 해당하는 소분류 이름 목록 가져옴
        List<String> subCategoryNames = em.createQuery("SELECT c.name FROM Category c WHERE c.parent.name = :middleCategoryName", String.class)
                .setParameter("middleCategoryName", middleCategoryName)
                .getResultList();

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null);

        // then
        assertThat(result).isNotEmpty();
        // 중분류 또는 소분류에 해당하는 상품인지 확인
        assertThat(result.getContent())
                .allMatch(product -> isCategoryOrSubcategoryName(middleCategoryName, subCategoryNames, product.getCategoryName()));
    }


    @DisplayName("홈페이지 메뉴바에서 원하는 소분류 카테고리 메뉴를 클릭하면 해당하는 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithConditionOfSubCategory() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        String subCategoryName = "코트"; // 소분류 카테고리 이름

        // 소분류 ID를 가져옴
        Long subCategoryId = em.createQuery("SELECT c.no FROM Category c WHERE c.name = :subCategoryName", Long.class)
                .setParameter("subCategoryName", subCategoryName)
                .getSingleResult();

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, subCategoryId, null);

        // then
        assertThat(result).isNotEmpty();

        // 반환된 상품들의 카테고리 이름이 소분류 카테고리 이름과 일치하는지 검증
        assertThat(result.getContent())
                .allMatch(product -> product.getCategoryName().equals(subCategoryName));
    }

    @DisplayName("상품들의 썸네일 이미지는 첫 번째(ord=1) 썸네일 이미지 URL이다.")
    @Test
    void testThumbnailImageIsFirstImage() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, null);

        // then
        assertThat(result).isNotEmpty();
        for (ProductListResponse product : result.getContent()) {
            // 실제 썸네일 이미지 URL
            String thumbnailUrl = product.getThumbNailImgUrl();

            // DB에서 해당 상품의 첫 번째 이미지 URL 조회
            String expectedThumbnailUrl = em.createQuery(
                            "SELECT img.imgUrl FROM ProductImg img WHERE img.product.no = :productId AND img.repimgYn = 'Y' AND img.ord = 1",
                            String.class)
                    .setParameter("productId", product.getNo())
                    .getSingleResult();

            // 검증: 썸네일 이미지 URL이 첫 번째 이미지 URL과 동일한지 확인
            assertThat(thumbnailUrl).isEqualTo(expectedThumbnailUrl);
        }
    }





    // 중분류 또는 소분류 이름인지 확인하는 메서드
    private boolean isCategoryOrSubcategoryName(String middleCategoryName, List<String> subCategoryNames, String productCategoryName) {
        // 중분류 이름 또는 소분류 이름에 해당하면 true 반환
        return middleCategoryName.equals(productCategoryName) || subCategoryNames.contains(productCategoryName);
    }
}