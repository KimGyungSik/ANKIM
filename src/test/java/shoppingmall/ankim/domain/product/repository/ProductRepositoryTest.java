package shoppingmall.ankim.domain.product.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.image.dto.ProductImgUrlResponse;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;
import java.util.stream.Collectors;

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


    @DisplayName("유저를 위한 상세 상품 페이지 단건 조회가 가능하다.")
    @Test
    void findProductUserDetailResponse() {
        // given
        Product product = ProductFactory.createProduct(em);

        // when
        ProductUserDetailResponse result = productRepository.findProductUserDetailResponse(product.getNo());

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


}