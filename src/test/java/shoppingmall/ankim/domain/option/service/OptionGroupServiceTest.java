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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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
        Product mockProduct = new Product();

        Product save = productRepository.save(mockProduct);

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
        List<OptionGroupResponse> optionGroups = optionGroupService.createOptionGroups(save.getNo(), List.of(colorGroupRequest, sizeGroupRequest));

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
        Product mockProduct = new Product();

        Product save = productRepository.save(mockProduct);

        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("options")
                .optionValues(List.of())
                .build();

        // when & then
        assertThrows(InsufficientOptionValuesException.class, () -> optionGroupService.createOptionGroups(save.getNo(), List.of(request)));

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
        Product product = productRepository.save(new Product());

        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Red").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(product.getNo(), List.of(request)).get(0);

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
    }

    @DisplayName("옵션 그룹을 삭제할 수 있다.")
    @Test
    void deleteOptionGroup() {
        // given
        Product product = productRepository.save(new Product());

        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Small").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(product.getNo(), List.of(request)).get(0);

        // when
        optionGroupService.deleteOptionGroup(createdGroup.getOptionGroupNo());

        // then
        assertThat(optionGroupRepository.findById(createdGroup.getOptionGroupNo())).isEmpty();
        assertThat(optionValueRepository.findById(createdGroup.getOptionValueResponses().get(0).getOptionValueNo())).isEmpty();
    }

    @DisplayName("옵션값을 삭제할 수 있다.")
    @Test
    void deleteOptionValue() {
        // given
        Product product = productRepository.save(new Product());

        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Blue").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(product.getNo(), List.of(request)).get(0);

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
}