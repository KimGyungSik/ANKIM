package shoppingmall.ankim.domain.option.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueResponse;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.exception.DuplicateOptionGroupException;
import shoppingmall.ankim.domain.option.exception.InsufficientOptionValuesException;
import shoppingmall.ankim.domain.option.exception.OptionGroupNotFoundException;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
class OptionGroupServiceTest {

    @Autowired
    OptionGroupService optionGroupService;

    @Autowired
    OptionGroupRepository optionGroupRepository;

    @Autowired
    OptionValueRepository optionValueRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager em;

    @DisplayName("옵션 그룹과 여러 옵션값(컬러와 사이즈)을 등록할 수 있다.")
    @Test
    void createOptionGroupsWithMultipleValues() {
        // given
        Product product = createProduct();
        productRepository.save(product);
        OptionValueCreateServiceRequest colorOption = OptionValueCreateServiceRequest.builder()
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();
        OptionValueCreateServiceRequest sizeOption = OptionValueCreateServiceRequest.builder()
                .valueName("Large")
                .colorCode(null)
                .build();

        OptionGroupCreateServiceRequest colorGroupRequest = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(colorOption))
                .build();

        OptionGroupCreateServiceRequest sizeGroupRequest = OptionGroupCreateServiceRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(sizeOption))
                .build();

        // when
        List<OptionGroupResponse> optionGroups = optionGroupService.createOptionGroups(product, List.of(colorGroupRequest, sizeGroupRequest));

        // then
        assertThat(optionGroups).isNotNull();
        assertThat(optionGroups).hasSize(2);

        // 컬러 옵션 그룹 검증
        OptionGroupResponse colorGroup = optionGroups.stream()
                .filter(group -> "컬러".equals(group.getGroupName()))
                .findFirst()
                .orElseThrow();
        OptionValueResponse color = colorGroup.getOptionValueResponses().get(0);
        assertThat(color.getValueName()).isEqualTo("Blue");
        assertThat(color.getColorCode()).isEqualTo("#0000FF");

        // 사이즈 옵션 그룹 검증
        OptionGroupResponse sizeGroup = optionGroups.stream()
                .filter(group -> "사이즈".equals(group.getGroupName()))
                .findFirst()
                .orElseThrow();
        OptionValueResponse size = sizeGroup.getOptionValueResponses().get(0);
        assertThat(size.getValueName()).isEqualTo("Large");
        assertThat(size.getColorCode()).isNull();

        // Repository에 저장되었는지 검증
        assertThat(optionGroupRepository.findAll()).hasSize(2);
        assertThat(optionValueRepository.findAll()).hasSize(2);
    }

    @DisplayName("옵션 그룹과 옵션값을 등록할 때 옵션값은 최소 1개가 필수이다.")
    @Test
    void createOptionGroupsWithoutOptionValue() {
        // given
        Product product = createProduct();
        productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("options")
                .optionValues(List.of())
                .build();

        // when & then
        assertThrows(InsufficientOptionValuesException.class, () -> optionGroupService.createOptionGroups(product, List.of(request)));

        // Repository에 저장되지 않았는지 검증
        assertThat(optionGroupRepository.findAll()).isEmpty();
        assertThat(optionValueRepository.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 옵션 그룹을 조회하면 예외가 발생한다.")
    @Test
    void getOptionGroup_NotFound() {
        assertThrows(OptionGroupNotFoundException.class, () -> optionGroupService.getOptionGroup(999L));
    }

    @DisplayName("옵션 그룹에 새로운 옵션값을 추가할 수 있다.")
    @Test
    void addOptionValueToOptionGroup() {
        // given
        Product product = createProduct();
        productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Red").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(product, List.of(request)).get(0);

        OptionValueCreateServiceRequest newOptionValueRequest = OptionValueCreateServiceRequest.builder()
                .valueName("Green")
                .colorCode("#00FF00")
                .build();

        // when
        OptionGroupResponse result = optionGroupService.addOptionValue(createdGroup.getOptionGroupNo(), newOptionValueRequest);

        // then
        assertThat(result.getOptionValueResponses()).hasSize(2)
                .extracting("valueName", "colorCode")
                .containsExactlyInAnyOrder(
                        tuple("Red", null),
                        tuple("Green", "#00FF00")
                );
        // Product 객체의 옵션 그룹 수 확인
        assertThat(product.getOptionGroups()).hasSize(1);

        // 첫 번째 옵션 그룹의 옵션 값 개수 확인
        assertThat(product.getOptionGroups().get(0).getOptionValues()).hasSize(2);
    }

    @DisplayName("옵션 그룹을 삭제할 수 있다.")
    @Test
    void deleteOptionGroup() {
        // given
        Product product = createProduct();
        productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Small").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(product, List.of(request)).get(0);

        // when
        optionGroupService.deleteOptionGroup(createdGroup.getOptionGroupNo());

        // then
        assertThat(optionGroupRepository.findById(createdGroup.getOptionGroupNo())).isEmpty();
    }

    @DisplayName("옵션값을 삭제할 수 있다.")
    @Test
    void deleteOptionValue() {
        // given
        Product product = createProduct();
        productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Blue").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(product, List.of(request)).get(0);

        OptionGroup optionGroup = optionGroupRepository.findById(createdGroup.getOptionGroupNo()).orElseThrow();
        OptionValue optionValue = optionGroup.getOptionValues().get(0);

        // when
        optionGroupService.deleteOptionValue(optionValue.getNo());

        em.flush();
        em.clear();

        // then
        assertThat(optionGroup.getOptionValues()).doesNotContain(optionValue); // 옵션 그룹에서 제거되었는지 확인
        assertThat(optionValueRepository.findById(optionValue.getNo())).isEmpty(); // DB에서도 제거되었는지 확인
    }

    @DisplayName("옵션 그룹 이름이 중복되면 예외가 발생한다")
    @Test
    void shouldThrowExceptionWhenOptionGroupNameIsDuplicate() {
        // given
        Product product = createProduct();
        productRepository.save(product);
        OptionGroupCreateServiceRequest request1 = OptionGroupCreateServiceRequest.builder()
                .groupName("색상")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Red").build()))
                .build();

        OptionGroupCreateServiceRequest request2 = OptionGroupCreateServiceRequest.builder()
                .groupName("색상") // 동일한 이름으로 중복된 옵션 그룹 생성
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Blue").build()))
                .build();

        // when & then
        optionGroupService.createOptionGroups(product, List.of(request1)); // 첫 번째 요청은 성공
        assertThatThrownBy(() ->
                optionGroupService.createOptionGroups(product, List.of(request2)) // 두 번째 요청은 예외 발생
        )
                .isInstanceOf(DuplicateOptionGroupException.class)
                .hasMessageContaining("옵션 항목이 중복되었습니다"); // 예외 메시지 확인
    }


    private Product createProduct() {
        return Product.builder()
                .name("Test Product")
                .build();
    }
}