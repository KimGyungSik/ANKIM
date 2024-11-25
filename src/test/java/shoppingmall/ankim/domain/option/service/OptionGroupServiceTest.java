package shoppingmall.ankim.domain.option.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.image.service.S3Service;
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
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueUpdateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class OptionGroupServiceTest {

    @MockBean
    InitProduct initProduct;

    @MockBean
    S3Service s3Service;

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
        Product save = productRepository.save(product);
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
        Product product = createProduct();
        Product save = productRepository.save(product);
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
        Product product = createProduct();
        Product save = productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Red").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(save.getNo(), List.of(request)).get(0);

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
        Product save = productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Small").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(save.getNo(), List.of(request)).get(0);

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
        Product save = productRepository.save(product);
        OptionGroupCreateServiceRequest request = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Blue").build()))
                .build();

        OptionGroupResponse createdGroup = optionGroupService.createOptionGroups(save.getNo(), List.of(request)).get(0);

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
        Product save = productRepository.save(product);
        OptionGroupCreateServiceRequest request1 = OptionGroupCreateServiceRequest.builder()
                .groupName("색상")
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Red").build()))
                .build();

        OptionGroupCreateServiceRequest request2 = OptionGroupCreateServiceRequest.builder()
                .groupName("색상") // 동일한 이름으로 중복된 옵션 그룹 생성
                .optionValues(List.of(OptionValueCreateServiceRequest.builder().valueName("Blue").build()))
                .build();

        // when & then
        optionGroupService.createOptionGroups(save.getNo(), List.of(request1)); // 첫 번째 요청은 성공
        assertThatThrownBy(() ->
                optionGroupService.createOptionGroups(save.getNo(), List.of(request2)) // 두 번째 요청은 예외 발생
        )
                .isInstanceOf(DuplicateOptionGroupException.class)
                .hasMessageContaining("옵션 항목이 중복되었습니다"); // 예외 메시지 확인
    }

    @Test
    @DisplayName("옵션 그룹 이름과 옵션 값이 함께 수정된다")
    void updateOptionGroupsNameAndValues() {
        // given
        Product product = productRepository.save(createProduct());
        OptionGroup optionGroup = OptionGroup.create("색상", product);
        OptionValue optionValue = OptionValue.create(optionGroup, "Red", "#FF0000");
        optionGroup.addOptionValue(optionValue);
        product.addOptionGroup(optionGroup);
        optionGroupRepository.save(optionGroup);

        // 수정 요청
        OptionValueUpdateServiceRequest updatedValueRequest = OptionValueUpdateServiceRequest.builder()
                .valueId(optionValue.getNo())
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();

        OptionGroupUpdateServiceRequest updateRequest = OptionGroupUpdateServiceRequest.builder()
                .groupId(optionGroup.getNo())
                .groupName("컬러")
                .optionValues(List.of(updatedValueRequest))
                .build();

        // when
        optionGroupService.updateOptionGroups(product.getNo(), List.of(updateRequest));

        // then
        OptionGroup updatedGroup = optionGroupRepository.findById(optionGroup.getNo()).orElseThrow();
        assertThat(updatedGroup.getName()).isEqualTo("컬러");
        OptionValue updatedValue = updatedGroup.getOptionValues().get(0);
        assertThat(updatedValue.getName()).isEqualTo("Blue");
        assertThat(updatedValue.getColorCode()).isEqualTo("#0000FF");
    }

    @Test
    @DisplayName("옵션 그룹 이름만 수정된다")
    void updateOptionGroupNameOnly() {
        // given
        Product product = productRepository.save(createProduct());
        OptionGroup optionGroup = OptionGroup.create("색상", product);
        product.addOptionGroup(optionGroup);
        optionGroupRepository.save(optionGroup);

        OptionGroupUpdateServiceRequest updateRequest = OptionGroupUpdateServiceRequest.builder()
                .groupId(optionGroup.getNo())
                .groupName("컬러")
                .optionValues(List.of()) // 옵션 값은 변경 없음
                .build();

        // when
        optionGroupService.updateOptionGroups(product.getNo(), List.of(updateRequest));

        // then
        OptionGroup updatedGroup = optionGroupRepository.findById(optionGroup.getNo()).orElseThrow();
        assertThat(updatedGroup.getName()).isEqualTo("컬러");
        assertThat(updatedGroup.getOptionValues()).isEmpty();
    }

    @Test
    @DisplayName("옵션 값만 수정된다")
    void updateOptionValuesOnly() {
        // given
        Product product = productRepository.save(createProduct());
        OptionGroup optionGroup = OptionGroup.create("색상", product);
        OptionValue optionValue = OptionValue.create(optionGroup, "Red", "#FF0000");
        optionGroup.addOptionValue(optionValue);
        product.addOptionGroup(optionGroup);
        optionGroupRepository.save(optionGroup);

        OptionValueUpdateServiceRequest updatedValueRequest = OptionValueUpdateServiceRequest.builder()
                .valueId(optionValue.getNo())
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();

        OptionGroupUpdateServiceRequest updateRequest = OptionGroupUpdateServiceRequest.builder()
                .groupId(optionGroup.getNo())
                .groupName(null) // 그룹 이름은 변경 없음
                .optionValues(List.of(updatedValueRequest))
                .build();

        // when
        optionGroupService.updateOptionGroups(product.getNo(), List.of(updateRequest));

        // then
        OptionValue updatedValue = optionValueRepository.findById(optionValue.getNo()).orElseThrow();
        assertThat(updatedValue.getName()).isEqualTo("Blue");
        assertThat(updatedValue.getColorCode()).isEqualTo("#0000FF");
    }

    @Test
    @DisplayName("새로운 옵션 그룹과 옵션 값이 추가된다")
    void addNewOptionGroupAndValues() {
        // given
        Product product = productRepository.save(createProduct());

        OptionValueUpdateServiceRequest newValueRequest = OptionValueUpdateServiceRequest.builder()
                .valueName("Green")
                .colorCode("#00FF00")
                .build();

        OptionGroupUpdateServiceRequest newGroupRequest = OptionGroupUpdateServiceRequest.builder()
                .groupName("새 옵션 그룹")
                .optionValues(List.of(newValueRequest))
                .build();

        // when
        optionGroupService.updateOptionGroups(product.getNo(), List.of(newGroupRequest));

        // then
        OptionGroup newGroup = optionGroupRepository.findAll().get(0);
        assertThat(newGroup.getName()).isEqualTo("새 옵션 그룹");
        assertThat(newGroup.getOptionValues()).hasSize(1);
        OptionValue newValue = newGroup.getOptionValues().get(0);
        assertThat(newValue.getName()).isEqualTo("Green");
        assertThat(newValue.getColorCode()).isEqualTo("#00FF00");
    }

    @Test
    @DisplayName("요청 데이터에 없는 기존 DB에만 존재하는 옵션 그룹은 삭제된다.")
    void deleteExistingGroupsNotInRequest() {
        // given
        Product product = productRepository.save(createProduct());

        // 기존 옵션 그룹 및 옵션 값 설정
        OptionGroup existingGroup1 = OptionGroup.create("기존 그룹 1", product);
        OptionValue existingValue1 = OptionValue.create(existingGroup1, "기존 옵션 값 1", null);
        existingGroup1.addOptionValue(existingValue1);
        product.addOptionGroup(existingGroup1);

        OptionGroup existingGroup2 = OptionGroup.create("기존 그룹 2", product);
        OptionValue existingValue2 = OptionValue.create(existingGroup2, "기존 옵션 값 2", null);
        existingGroup2.addOptionValue(existingValue2);
        product.addOptionGroup(existingGroup2);

        optionGroupRepository.saveAll(List.of(existingGroup1, existingGroup2));

        // 새로운 요청 데이터 (기존 그룹 1만 포함)
        OptionGroupUpdateServiceRequest request1 = OptionGroupUpdateServiceRequest.builder()
                .groupId(existingGroup1.getNo()) // 기존 그룹 1 유지
                .groupName("기존 그룹 1 수정됨") // 이름 변경
                .optionValues(List.of(
                        OptionValueUpdateServiceRequest.builder()
                                .valueId(existingValue1.getNo()) // 기존 옵션 값 유지
                                .valueName("기존 옵션 값 1 수정됨") // 이름 변경
                                .colorCode("#FF0000") // 색상 변경
                                .build()
                ))
                .build();

        // 새로운 요청 데이터 (새로운 그룹 추가)
        OptionGroupUpdateServiceRequest request2 = OptionGroupUpdateServiceRequest.builder()
                .groupId(null) // 새로운 그룹 추가
                .groupName("새로운 그룹")
                .optionValues(List.of(
                        OptionValueUpdateServiceRequest.builder()
                                .valueId(null) // 새로운 옵션 값 추가
                                .valueName("새로운 옵션 값")
                                .colorCode("#FFFFFF")
                                .build()
                ))
                .build();

        // when
        optionGroupService.updateOptionGroups(product.getNo(), List.of(request1, request2));

        // then
        // 기존 그룹 1은 수정되었는지 확인
        OptionGroup updatedGroup1 = optionGroupRepository.findById(existingGroup1.getNo()).orElseThrow();
        assertThat(updatedGroup1.getName()).isEqualTo("기존 그룹 1 수정됨");
        OptionValue updatedValue1 = updatedGroup1.getOptionValues().get(0);
        assertThat(updatedValue1.getName()).isEqualTo("기존 옵션 값 1 수정됨");
        assertThat(updatedValue1.getColorCode()).isEqualTo("#FF0000");

        // 기존 그룹 2는 삭제되었는지 확인
        assertThat(optionGroupRepository.findById(existingGroup2.getNo())).isEmpty();
        assertThat(optionValueRepository.findById(existingValue2.getNo())).isEmpty();

        // 새로운 그룹이 추가되었는지 확인
        OptionGroup newGroup = optionGroupRepository.findAll().stream()
                .filter(group -> group.getName().equals("새로운 그룹"))
                .findFirst()
                .orElseThrow();
        OptionValue newValue = newGroup.getOptionValues().get(0);
        assertThat(newValue.getName()).isEqualTo("새로운 옵션 값");
        assertThat(newValue.getColorCode()).isEqualTo("#FFFFFF");

        // 전체 그룹 및 옵션 값 개수 확인
        assertThat(optionGroupRepository.findAll()).hasSize(2); // 기존 그룹 1 + 새로운 그룹
        assertThat(optionValueRepository.findAll()).hasSize(2); // 기존 옵션 값 1 + 새로운 옵션 값
    }

    @Test
    @DisplayName("요청 데이터에 없는 기존 DB에만 존재하는 옵션 값은 삭제된다.")
    void deleteExistingAndAddNewOptionValue() {
        // given
        Product product = productRepository.save(createProduct());

        // 기존 옵션 그룹과 옵션 값 설정
        OptionGroup existingGroup = OptionGroup.create("기존 그룹", product);
        OptionValue existingValue1 = OptionValue.create(existingGroup, "기존 옵션 값 1", "#FF0000");
        OptionValue existingValue2 = OptionValue.create(existingGroup, "기존 옵션 값 2", "#00FF00");
        existingGroup.addOptionValue(existingValue1);
        existingGroup.addOptionValue(existingValue2);
        product.addOptionGroup(existingGroup);
        optionGroupRepository.save(existingGroup);

        // 요청 데이터: 기존 옵션 값 2는 삭제하고, 새로운 옵션 값 2 추가
        OptionGroupUpdateServiceRequest updateRequest = OptionGroupUpdateServiceRequest.builder()
                .groupId(existingGroup.getNo()) // 기존 그룹 ID
                .groupName("기존 그룹 수정됨") // 그룹 이름 수정
                .optionValues(List.of(
                        OptionValueUpdateServiceRequest.builder()
                                .valueId(existingValue1.getNo()) // 기존 옵션 값 1 유지
                                .valueName("기존 옵션 값 1 수정됨")
                                .colorCode("#FFFFFF")
                                .build(),
                        OptionValueUpdateServiceRequest.builder()
                                .valueId(null) // 새로운 옵션 값 추가
                                .valueName("새로운 옵션 값 2")
                                .colorCode("#0000FF")
                                .build()
                ))
                .build();

        // when
        optionGroupService.updateOptionGroups(product.getNo(), List.of(updateRequest));

        // then
        // 기존 옵션 값 1은 수정되었는지 확인
        OptionValue updatedValue1 = optionValueRepository.findById(existingValue1.getNo()).orElseThrow();
        assertThat(updatedValue1.getName()).isEqualTo("기존 옵션 값 1 수정됨");
        assertThat(updatedValue1.getColorCode()).isEqualTo("#FFFFFF");

        // 기존 옵션 값 2는 삭제되었는지 확인
        assertThat(optionValueRepository.findById(existingValue2.getNo())).isEmpty();

        // 새로운 옵션 값 2가 추가되었는지 확인
        OptionGroup updatedGroup = optionGroupRepository.findById(existingGroup.getNo()).orElseThrow();
        List<OptionValue> updatedValues = updatedGroup.getOptionValues();
        assertThat(updatedValues).hasSize(2);
        assertThat(updatedValues).anySatisfy(value -> {
            assertThat(value.getName()).isEqualTo("새로운 옵션 값 2");
            assertThat(value.getColorCode()).isEqualTo("#0000FF");
        });

        // 그룹 이름이 수정되었는지 확인
        assertThat(updatedGroup.getName()).isEqualTo("기존 그룹 수정됨");
    }


    private Product createProduct() {
        return Product.builder()
                .name("Test Product")
                .discRate(20)
                .origPrice(100)
                .build();
    }
}