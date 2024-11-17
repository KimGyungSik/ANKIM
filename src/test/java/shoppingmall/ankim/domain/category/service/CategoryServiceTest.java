package shoppingmall.ankim.domain.category.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.controller.request.CategoryCreateRequest;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.exception.CategoryLinkedWithProductException;
import shoppingmall.ankim.domain.category.exception.ChildCategoryExistsException;
import shoppingmall.ankim.domain.category.exception.DuplicateMiddleCategoryNameException;
import shoppingmall.ankim.domain.category.exception.DuplicateSubCategoryNameException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;
import shoppingmall.ankim.domain.category.service.request.CategoryUpdateServiceRequest;
import shoppingmall.ankim.domain.image.service.ProductImgService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.config.S3Config;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.MIDDLE;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.SUB;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.profiles.active=test" // "test" 프로파일 활성화
})
class CategoryServiceTest {

    @MockBean
    private S3Service s3Service;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryQueryService categoryQueryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager em;

    @DisplayName("카테고리에 새로운 중분류를 추가할 수 있다")
    @Test
    void createCategoryTest1() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder()
                .name("상의")
                .build();

        // when
        CategoryResponse result = categoryService.createCategory(categoryCreateRequest.toServiceRequest());

        // then
        assertThat(result.getCategoryNo()).isNotNull();
        assertThat(result)
                .extracting("name", "level")
                .contains("상의", MIDDLE);
    }

    @DisplayName("카테고리에 이미 존재하는 중분류에 소분류를 추가할 수 있다")
    @Test
    void createCategoryTest2() {
        // given
        Category categoryMiddle = categoryRepository.save(Category.create("상의"));
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder()
                .name("티셔츠")
                .parentNo(categoryMiddle.getNo())
                .build();

        // when
        CategoryResponse result = categoryService.createCategory(categoryCreateRequest.toServiceRequest());

        // then
        assertThat(result.getCategoryNo()).isNotNull();
        assertThat(result)
                .extracting("name", "level")
                .contains("상의", MIDDLE);

        // 하위 카테고리 목록 확인
        assertThat(result.getChildCategories())
                .hasSize(1)
                .extracting("name", "level", "parentNo")
                .containsExactly(
                        tuple("티셔츠", SUB, result.getCategoryNo())
                );
    }

    @DisplayName("카테고리에 이미 존재하는 중분류에 여러개의 소분류를 추가할 수 있다")
    @Test
    void createCategoryTest2_1() {
        // given
        Category categoryMiddle = categoryRepository.save(Category.create("상의"));
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder()
                .parentNo(categoryMiddle.getNo())
                .childCategories(List.of(
                        createRequest("티셔츠"),
                        createRequest("셔츠"),
                        createRequest("반팔")))
                .build();

        // when
        CategoryResponse result = categoryService.createCategory(categoryCreateRequest.toServiceRequest());

        // then
        assertThat(result.getCategoryNo()).isNotNull();
        assertThat(result)
                .extracting("name", "level")
                .contains("상의", MIDDLE);

        // 하위 카테고리 목록 확인
        assertThat(result.getChildCategories())
                .hasSize(3)
                .extracting("name", "level", "parentNo")
                .containsExactly(
                        tuple("티셔츠", SUB, result.getCategoryNo()),
                        tuple("셔츠", SUB, result.getCategoryNo()),
                        tuple("반팔", SUB, result.getCategoryNo())
                );
    }


    @DisplayName("카테고리에 중분류와 소분류를 동시에 추가할 수 있다")
    @Test
    void createCategoryTest3() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder()
                .name("상의")
                .childCategories(List.of(
                        createRequest("티셔츠"),
                        createRequest("셔츠"),
                        createRequest("반팔")))
                .build();

        // when
        CategoryResponse result = categoryService.createCategory(categoryCreateRequest.toServiceRequest());

        // then
        assertThat(result.getCategoryNo()).isNotNull();
        assertThat(result)
                .extracting("name", "level")
                .contains("상의", MIDDLE);

        // 하위 카테고리 목록 확인
        assertThat(result.getChildCategories())
                .hasSize(3)
                .extracting("name", "level", "parentNo")
                .containsExactly(
                        tuple("티셔츠", SUB, result.getCategoryNo()),
                        tuple("셔츠", SUB, result.getCategoryNo()),
                        tuple("반팔", SUB, result.getCategoryNo())
                );
    }

    @DisplayName("삭제하고 싶은 카테고리에 상품이 존재할 경우 예외가 발생한다.")
    @Test
    void deleteCategoryLinkedWithProductException() {
        // given
        Category sub1 = Category.create("티셔츠");
        Category sub2 = Category.create("셔츠");
        Category sub3 = Category.create("반팔");
        Category middle = Category.builder()
                .name("상의")
                .subCategories(List.of(sub1, sub2, sub3))
                .build();
        Category middleCategory = categoryRepository.save(middle);

        Product product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .category(middleCategory)
                .build());


        // when // then
        assertThatThrownBy(() -> categoryService.deleteCategory(middleCategory.getNo()))
                .isInstanceOf(CategoryLinkedWithProductException.class)
                .hasMessage("해당 카테고리에 속한 상품이 존재하므로 삭제할 수 없습니다.");
    }

    @DisplayName("삭제하고 싶은 소분류에 상품이 없다면 소분류를 선택해서 삭제할 수 있다")
    @Test
    void deleteCategory() {
        // given
        Category sub1 = Category.create("티셔츠");
        Category sub2 = Category.create("셔츠");
        Category sub3 = Category.create("반팔");
        Category middle = Category.builder()
                .name("상의")
                .subCategories(List.of(sub1, sub2, sub3))
                .build();
        categoryRepository.save(middle);
        Long sub1Id = sub1.getNo();
        Long middleId = middle.getNo();

        // when
        categoryService.deleteCategory(sub1Id);

        // then
        List<CategoryResponse> result = categoryQueryService.getSubCategoriesUnderMiddleCategory(middleId);
        assertThat(result)
                .hasSize(2)
                .extracting("name", "level", "parentNo")
                .containsExactly(
                        tuple("셔츠", SUB, middle.getNo()),
                        tuple("반팔", SUB, middle.getNo())
                );
    }

    @DisplayName("삭제하고 싶은 중분류에 상품이 없고 소분류가 없다면 선택한 중분류를 삭제할 수 있다")
    @Test
    void deleteCategory2() {
        // given
        Category categoryMiddle = categoryRepository.save(Category.create("상의"));
        assertThat(categoryQueryService.retrieveMiddleCategories()).hasSize(1);

        // when
        categoryService.deleteCategory(categoryMiddle.getNo());

        // then
        assertThat(categoryQueryService.retrieveMiddleCategories()).hasSize(0);
    }

    @DisplayName("중분류를 삭제하려고 할 때 소분류가 존재하면 예외가 발생한다.")
    @Test
    void deleteCategory3() {
        // given
        Category sub1 = Category.create("티셔츠");
        Category sub2 = Category.create("셔츠");
        Category sub3 = Category.create("반팔");
        Category middle = Category.builder()
                .name("상의")
                .subCategories(List.of(sub1, sub2, sub3))
                .build();
        Category middleCategory = categoryRepository.save(middle);

        // when // then
        assertThatThrownBy(() -> categoryService.deleteCategory(middleCategory.getNo()))
                .isInstanceOf(ChildCategoryExistsException.class)
                .hasMessage("삭제할 중분류에 소분류가 존재하므로 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("중복된 중분류 이름으로 카테고리 생성 시 예외가 발생한다")
    void createCategory_withDuplicateMiddleCategoryName_throwsException() {
        // given
        CategoryCreateServiceRequest middleCategoryRequest = CategoryCreateServiceRequest.builder()
                .name("상의")
                .build();

        categoryService.createCategory(middleCategoryRequest);  // 중분류 생성

        // when // then
        assertThatThrownBy(() -> categoryService.createCategory(middleCategoryRequest))
                .isInstanceOf(DuplicateMiddleCategoryNameException.class)
                .hasMessage("중복된 중분류 이름이 존재합니다.");
    }

    @Test
    @DisplayName("중복된 소분류 이름으로 카테고리 생성 시 예외가 발생한다")
    void createCategory_withDuplicateSubCategoryName_throwsException() {
        // given
        Category parentCategory = categoryRepository.save(Category.create("상의")); // 중분류 생성

        CategoryCreateServiceRequest subCategoryRequest = CategoryCreateServiceRequest.builder()
                .name("티셔츠")
                .parentNo(parentCategory.getNo())
                .build();
        categoryService.createCategory(subCategoryRequest);

        // when // then
        assertThatThrownBy(() -> categoryService.createCategory(subCategoryRequest))
                .isInstanceOf(DuplicateSubCategoryNameException.class)
                .hasMessage("해당 중분류 아래에 중복된 소분류 이름이 존재합니다.");
    }


    @DisplayName("원하는 카테고리(중분류)의 이름을 수정할 수 있다.")
    @Test
    void updateCategoryWithMiddle() {
        // given
        Category category = categoryRepository.save(Category.create("상의")); // 중분류 생성
        CategoryUpdateServiceRequest request = createUpdateRequest("새로운 상의");
        Product product = productRepository.save(createProduct(category));

        // when
        categoryService.updateMiddleCategory(category.getNo(), request);

        // then
        Category updatedCategory = categoryRepository.findById(category.getNo()).orElseThrow();
        assertThat(updatedCategory.getName()).isEqualTo(request.getName());

        Product updatedProduct = productRepository.findById(product.getNo()).orElseThrow();
        assertThat(updatedProduct.getCategory().getName()).isEqualTo(request.getName());
    }

    @DisplayName("원하는 카테고리(소분류)의 이름을 수정할 수 있다.")
    @Test
    void updateCategoryWithSub() {
        // given
        Category sub1 = Category.create("티셔츠");
        Category sub2 = Category.create("셔츠");
        Category sub3 = Category.create("반팔");
        Category middle = Category.builder()
                .name("상의")
                .subCategories(List.of(sub1, sub2, sub3))
                .build();
        categoryRepository.save(middle);
        CategoryUpdateServiceRequest request = createUpdateRequest("새로운 티셔츠");
        Product product = productRepository.save(createProduct(sub1));

        // when
        categoryService.updateSubCategory(sub1.getNo(), request);

        // then
        Category updatedSubCategory = categoryRepository.findById(sub1.getNo()).orElseThrow();
        assertThat(updatedSubCategory.getName()).isEqualTo(request.getName());

        Product updatedProduct = productRepository.findById(product.getNo()).orElseThrow();
        assertThat(updatedProduct.getCategory().getName()).isEqualTo(request.getName());
    }

    @DisplayName("원하는 소분류 카테고리를 새로운 중분류에 속하게 할 수 있다.")
    @Test
    void updateCategoryWithChangeParentCategory() {
        // given
        Category sub1 = Category.create("티셔츠");
        Category middle1 = Category.builder().name("상의1").subCategories(List.of(sub1)).build();
        Category middle2 = Category.builder().name("상의2").build();
        categoryRepository.save(middle1);
        categoryRepository.save(middle2);

        CategoryUpdateServiceRequest request = CategoryUpdateServiceRequest.builder()
                .newParentNo(middle2.getNo())
                .build();
        Product product = productRepository.save(createProduct(sub1));

        // when
        categoryService.updateSubCategory(sub1.getNo(), request);

        // then
        Category updatedSubCategory = categoryRepository.findById(sub1.getNo()).orElseThrow();
        assertThat(updatedSubCategory.getParent().getNo()).isEqualTo(middle2.getNo());

        Product updatedProduct = productRepository.findById(product.getNo()).orElseThrow();
        assertThat(updatedProduct.getCategory().getParent().getNo()).isEqualTo(middle2.getNo());
    }

    @DisplayName("카테고리를 수정하면 해당 카테고리에 속해 있는 상품의 카테고리도 자동 변경된다.")
    @Test
    void updateCategoryForProducts() {
        // given
        Category category = categoryRepository.save(Category.create("상의")); // 중분류 생성
        CategoryUpdateServiceRequest request = createUpdateRequest("새로운 상의");
        Product product1 = productRepository.save(createProduct(category));
        Product product2 = productRepository.save(createProduct(category));

        // when
        categoryService.updateMiddleCategory(category.getNo(), request);

        // then
        Product updatedProduct1 = productRepository.findById(product1.getNo()).orElseThrow();
        Product updatedProduct2 = productRepository.findById(product2.getNo()).orElseThrow();

        assertThat(updatedProduct1.getCategory().getName()).isEqualTo(request.getName());
        assertThat(updatedProduct2.getCategory().getName()).isEqualTo(request.getName());
    }


    private CategoryCreateRequest createRequest(String name) {
        return CategoryCreateRequest.builder()
                .name(name)
                .build();
    }

    private CategoryUpdateServiceRequest createUpdateRequest(String name) {
        return CategoryUpdateServiceRequest.builder()
                .name(name)
                .build();
    }

    private Product createProduct(Category category) {
        return Product.builder()
                .category(category)
                .name("테스트 상품")
                .code("PROD123")
                .desc("테스트 상품 설명")
                .discRate(10)
                .sellPrice(10000)
                .origPrice(12000)
                .optYn("Y")
                .restockYn("N")
                .qty(100)
                .bestYn("N")
                .freeShip("Y")
                .shipFee(2500)
                .searchKeywords("테스트")
                .relProdCode("REL001")
                .cauProd("주의사항")
                .cauOrd("주문 유의사항")
                .cauShip("배송 유의사항")
                .build();
    }
}




