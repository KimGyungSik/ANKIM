package shoppingmall.ankim.domain.product.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import shoppingmall.ankim.domain.product.repository.query.helper.OrderBy;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static shoppingmall.ankim.factory.ProductFactory.createSearchTestProducts;
import static shoppingmall.ankim.factory.ProductFactory.createTestProductsWithColorOptions;


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
        createSearchTestProducts(em);
        createTestProductsWithColorOptions(em, 10);
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
        assertThat(items.get(0).getQty()).isEqualTo(100);
        assertThat(items.get(1).getQty()).isEqualTo(100);
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
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null,null,null,null,null,null);

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
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null,null,null,null,null,null);

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
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null,null,null,null,null,null);

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
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null,null,null,null,null,null);

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
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, null, null,null,null,null,null,null);

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
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, subCategoryId, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 반환된 상품들의 카테고리 이름이 소분류 카테고리 이름과 일치하는지 검증
        assertThat(result.getContent())
                .allMatch(product -> product.getCategoryName().equals(subCategoryName));
    }

    @DisplayName("중분류 카테고리(TOP)의 결과를 2차로 소분류 카테고리(티셔츠)로 필터링하여 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithCategoryFilter() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Condition condition = Condition.TOP; // 중분류 카테고리

        String subCategoryName = "티셔츠"; // 소분류 카테고리

        // 소분류 ID를 가져옴
        Long subCategoryId = em.createQuery("SELECT c.no FROM Category c WHERE c.name = :subCategoryName", Long.class)
                .setParameter("subCategoryName", subCategoryName)
                .getSingleResult();


        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, null, subCategoryId, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 반환된 상품들의 카테고리 이름이 소분류 카테고리 이름과 일치하는지 검증
        assertThat(result.getContent())
                .allMatch(product -> product.getCategoryName().equals(subCategoryName));
    }


    @DisplayName("검색 키워드가 NEW이고 카테고리(티셔츠)로 필터링하면 관련된 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithCategoryFilterWithKeyword() {
        // given
        PageRequest pageable = PageRequest.of(0, 20);
        String keyword = "NEW"; // 검색 키워드
        String categoryName = "티셔츠"; // 필터링할 카테고리 이름

        // 카테고리 ID 가져오기
        Long categoryId = em.createQuery("SELECT c.no FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", categoryName)
                .getSingleResult();

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, categoryId, keyword,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(10);
        // 검증: 반환된 상품들이 검색 키워드에 일치하거나, 카테고리에 속해 있는지 확인
        assertThat(result.getContent())
                .allMatch(product ->
                        product.getName().toLowerCase().contains(keyword.toLowerCase()) || // 상품명에 포함
                                product.getDesc().toLowerCase().contains(keyword.toLowerCase()) || // 상세 설명에 포함
                                product.getCategoryName().equals(categoryName)); // 카테고리가 티셔츠
    }


    @DisplayName("정렬 조건은 높은 가격순이고 카테고리(티셔츠)로 필터링하면 관련된 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithCategoryFilterWithOrderBy() {
        // given
        PageRequest pageable = PageRequest.of(0, 20);
        OrderBy order = OrderBy.HIGH_PRICE;
        String categoryName = "티셔츠"; // 필터링할 카테고리 이름

        // 카테고리 ID 가져오기
        Long categoryId = em.createQuery("SELECT c.no FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", categoryName)
                .getSingleResult();
        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, categoryId, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 필터링 검증: 반환된 상품들이 지정한 카테고리에 속하는지 확인
        assertThat(result.getContent())
                .allMatch(product -> product.getCategoryName().equals(categoryName));

        // 정렬 검증: 높은 가격순으로 정렬되었는지 Streams API로 확인
        List<Integer> prices = result.getContent().stream()
                .map(ProductListResponse::getSellPrice)
                .toList();

        assertThat(prices).isSortedAccordingTo((a, b) -> Integer.compare(b, a)); // 높은 가격순 검증
    }
    @DisplayName("검색 키워드와 높은 가격순 정렬을 적용하고 카테고리(티셔츠)로 필터링하면 관련된 상품들을 볼 수 있다.")
    @Test
    void findUserProductListResponseWithCategoryFilterWithKeywordAndOrderBy() {
        // given
        PageRequest pageable = PageRequest.of(0, 20); // 페이지 요청 (1페이지, 20개씩)
        String keyword = "NEW"; // 검색 키워드
        OrderBy order = OrderBy.HIGH_PRICE; // 높은 가격순 정렬
        String categoryName = "티셔츠"; // 필터링할 카테고리 이름

        // 카테고리 ID 가져오기
        Long categoryId = em.createQuery("SELECT c.no FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", categoryName)
                .getSingleResult();

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, categoryId, keyword,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 필터링 검증: 반환된 상품들이 검색 키워드와 지정한 카테고리에 속하는지 확인
        assertThat(result.getContent())
                .allMatch(product ->
                        (product.getName().toLowerCase().contains(keyword.toLowerCase()) || // 상품명에 포함
                                product.getDesc().toLowerCase().contains(keyword.toLowerCase())) // 상세 설명에 포함
                                && product.getCategoryName().equals(categoryName)); // 카테고리가 티셔츠

        // 정렬 검증: 높은 가격순으로 정렬되었는지 Streams API로 확인
        List<Integer> prices = result.getContent().stream()
                .map(ProductListResponse::getSellPrice)
                .toList();

        assertThat(prices).isSortedAccordingTo((a, b) -> Integer.compare(b, a)); // 높은 가격순 검증
    }
    @DisplayName("상품들의 썸네일 이미지는 첫 번째(ord=1) 썸네일 이미지 URL이다.")
    @Test
    void testThumbnailImageIsFirstImage() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();
        // DB에서 상품 ID와 첫 번째 썸네일 URL을 매핑
        Map<Long, String> expectedThumbnails = em.createQuery(
                        "SELECT img.product.no, img.imgUrl FROM ProductImg img WHERE img.repimgYn = 'Y' AND img.ord = 1",
                        Object[].class)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // 상품 ID
                        row -> (String) row[1] // 썸네일 URL
                ));

        // 상품 ID를 기준으로 실제와 예상 썸네일 비교
        result.getContent().forEach(product ->
                assertThat(product.getThumbNailImgUrl())
                        .isEqualTo(expectedThumbnails.get(product.getNo())));
    }

    @DisplayName("NEW라고 검색을 했을 때 상품명에 NEW가 들어간 상품들을 모두 볼 수 있다.")
    @Test
    void findUserProductListResponseWithKeywordFilterWithProductName() {
        // given
        PageRequest pageable = PageRequest.of(0, 70);
        String keyword = "NEW"; // 검색 키워드

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, keyword,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(61);
        assertThat(result.getContent())
                .allMatch(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()));
    }


    @DisplayName("RED라고 검색을 했을 때 상품 색상이 RED인 상품들을 모두 볼 수 있다.")
    @Test
    void findUserProductListResponseWithKeywordFilterWithSearchKeywords() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        String keyword = "RED"; // 검색 키워드

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, keyword,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getContent())
                .allMatch(product -> product.getSearchKeywords().toLowerCase().contains(keyword.toLowerCase()));
    }


    @DisplayName("'코튼재질'이라고 검색을 했을 때 상품 상세설명에 '코튼재질'이 들어간 상품들을 모두 볼 수 있다.")
    @Test
    void findUserProductListResponseWithKeywordFilterWithProductDesc() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        String keyword = "코튼재질"; // 검색 키워드

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, keyword,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getContent())
                .allMatch(product -> product.getDesc().contains(keyword));
    }


    @DisplayName("상품들을 최신순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByLATEST() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.LATEST;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 최신순 정렬 검증
        List<LocalDateTime> createdDates = result.getContent().stream()
                .map(ProductListResponse::getCreatedAt)
                .toList();

        assertThat(createdDates).isSortedAccordingTo((d1, d2) -> d2.compareTo(d1)); // 최신순 검증
    }


    @DisplayName("상품들을 인기순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByPOPULAR() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.POPULAR;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 인기순 검증
        List<Integer> wishCounts = result.getContent().stream()
                .map(ProductListResponse::getWishCnt)
                .toList();

        assertThat(wishCounts).isSortedAccordingTo((w1, w2) -> Integer.compare(w2, w1)); // 인기순 검증
    }

    @DisplayName("상품들을 낮은 가격순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByLOW_PRICE() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.LOW_PRICE;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 낮은 가격순 검증
        List<Integer> sellPrices = result.getContent().stream()
                .map(ProductListResponse::getSellPrice)
                .toList();

        assertThat(sellPrices).isSorted();
    }

    @DisplayName("상품들을 높은 가격순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByHIGH_PRICE() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.HIGH_PRICE;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 높은 가격순 검증
        List<Integer> sellPrices = result.getContent().stream()
                .map(ProductListResponse::getSellPrice)
                .toList();

        assertThat(sellPrices).isSortedAccordingTo((p1, p2) -> Integer.compare(p2, p1));
    }

    @DisplayName("상품들을 높은 할인율순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByHIGH_DISCOUNT_RATE() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.HIGH_DISCOUNT_RATE;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 높은 할인율 검증
        List<Integer> discountRates = result.getContent().stream()
                .map(ProductListResponse::getDiscRate)
                .toList();

        assertThat(discountRates).isSortedAccordingTo((d1, d2) -> Integer.compare(d2, d1));
    }

    @DisplayName("상품들을 리뷰 많은순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByHIGH_REVIEW() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.HIGH_REVIEW;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 리뷰 많은 순 검증
        List<Integer> reviewCounts = result.getContent().stream()
                .map(ProductListResponse::getRvwCnt)
                .filter(Objects::nonNull) // Null 값 필터링
                .toList();

        assertThat(reviewCounts).isSortedAccordingTo((r1, r2) -> Integer.compare(r2, r1));
    }

    @DisplayName("상품들을 조회수 많은순 정렬로 볼 수 있다.")
    @Test
    void findUserProductListResponseWithOrderByHIGH_VIEW() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        OrderBy order = OrderBy.HIGH_VIEW;

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, order, null, null,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 조회수 많은 순 검증
        List<Integer> viewCounts = result.getContent().stream()
                .map(ProductListResponse::getViewCnt)
                .filter(Objects::nonNull) // Null 값 필터링
                .toList();

        assertThat(viewCounts).isSortedAccordingTo((v1, v2) -> Integer.compare(v2, v1));
    }

    @DisplayName("검색 키워드, 카테고리, 정렬 조건, 필터링 조건을 모두 만족하는 상품들을 조회한다.")
    @ParameterizedTest
    @CsvSource({
            "NEW, 티셔츠, HIGH_REVIEW, NEW",      // 키워드 "NEW", 카테고리 "티셔츠", 리뷰 많은 순, 최신 상품
            "BEST, 맨투맨, HIGH_PRICE, BEST",     // 키워드 "BEST", 카테고리 "맨투맨", 높은 가격 순, 베스트 상품
            "DISCOUNT, 자켓, LOW_PRICE, DISCOUNT" // 키워드 "DISCOUNT", 카테고리 "자켓", 낮은 가격 순, 할인 상품
    })
    void findUserProductListResponseWithAllConditions(
            String keyword,
            String categoryName,
            OrderBy order,
            Condition condition) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // 카테고리 ID 가져오기
        Long categoryId = em.createQuery("SELECT c.no FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", categoryName)
                .getSingleResult();

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, condition, order, categoryId, keyword,null,null,null,null,null);

        // then
        assertThat(result).isNotEmpty();

        // 검증 1: 검색 키워드 만족
        assertThat(result.getContent())
                .allMatch(product ->
                        product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                product.getDesc().toLowerCase().contains(keyword.toLowerCase()) ||
                                product.getSearchKeywords().toLowerCase().contains(keyword.toLowerCase()));

        // 검증 2: 카테고리 필터링 만족
        assertThat(result.getContent())
                .allMatch(product -> product.getCategoryName().equals(categoryName));

        // 검증 3: 정렬 조건 만족
        if (ORDER_VALIDATORS.containsKey(order)) {
            List<ProductListResponse> products = result.getContent();
            assertThat(products).isSortedAccordingTo(ORDER_VALIDATORS.get(order));
        }

        // 검증 4: 필터링 조건 만족
        if (CONDITION_VALIDATORS.containsKey(condition)) {
            assertThat(result.getContent())
                    .allMatch(CONDITION_VALIDATORS.get(condition));
        }
    }


    private static final Map<OrderBy, Comparator<ProductListResponse>> ORDER_VALIDATORS = Map.of(
            OrderBy.HIGH_REVIEW, Comparator.comparing(ProductListResponse::getRvwCnt, Comparator.nullsLast(Comparator.reverseOrder())),
            OrderBy.HIGH_PRICE, Comparator.comparing(ProductListResponse::getSellPrice, Comparator.nullsLast(Comparator.reverseOrder())),
            OrderBy.LOW_PRICE, Comparator.comparing(ProductListResponse::getSellPrice, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    private static final Map<Condition, Predicate<ProductListResponse>> CONDITION_VALIDATORS = Map.of(
            Condition.NEW, product -> product.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1)),
            Condition.BEST, product -> product.getWishCnt() >= 30,
            Condition.HANDMADE, product -> "Y".equals(product.getHandMadeYn()),
            Condition.DISCOUNT, product -> product.getDiscRate() > 0
    );


    // 중분류 또는 소분류 이름인지 확인하는 메서드
    private boolean isCategoryOrSubcategoryName(String middleCategoryName, List<String> subCategoryNames, String productCategoryName) {
        // 중분류 이름 또는 소분류 이름에 해당하면 true 반환
        return middleCategoryName.equals(productCategoryName) || subCategoryNames.contains(productCategoryName);
    }
}