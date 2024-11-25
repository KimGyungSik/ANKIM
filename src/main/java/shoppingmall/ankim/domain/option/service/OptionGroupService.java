package shoppingmall.ankim.domain.option.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.exception.DuplicateOptionGroupException;
import shoppingmall.ankim.domain.option.exception.InsufficientOptionValuesException;
import shoppingmall.ankim.domain.option.exception.OptionValueNotFoundException;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.exception.OptionGroupNotFoundException;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueUpdateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionGroupService {

    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;
    private final ProductRepository productRepository;

    public List<OptionGroupResponse> createOptionGroups(Long productId, List<OptionGroupCreateServiceRequest> requests) {
        Product product = getProductWithOptionGroups(productId);
        List<OptionGroupResponse> optionGroupResponses = new ArrayList<>();

        for (OptionGroupCreateServiceRequest request : requests) {
            boolean isDuplicate = product.getOptionGroups().stream()
                    .anyMatch(existingGroup -> existingGroup.getName().equals(request.getGroupName()));

            if (isDuplicate) {
                throw new DuplicateOptionGroupException(DUPLICATE_OPTION_GROUP);
            }

            // OptionValue가 최소 1개 이상 존재하는지 확인
            if (request.getOptionValues() == null || request.getOptionValues().isEmpty()) {
                throw new InsufficientOptionValuesException(INSUFFICIENT_OPTION_VALUES);
            }

            // OptionGroup 생성
            OptionGroup optionGroup = OptionGroup.create(request.getGroupName(), product);

            // OptionValue 목록 생성 및 추가
            request.getOptionValues().forEach(optValReq -> {
                OptionValue optionValue = OptionValue.create(optionGroup, optValReq.getValueName(), optValReq.getColorCode());
                optionGroup.addOptionValue(optionValue);
            });

            product.addOptionGroup(optionGroup);
            OptionGroup saveOptionGroup = optionGroupRepository.save(optionGroup);
            optionGroupResponses.add(OptionGroupResponse.of(saveOptionGroup));
        }

        return optionGroupResponses;
    }

    /*
     옵션 수정
     옵션 그룹명 및 옵션 값 수정 가능
     옵션 그룹 수정
     * 고려해야할 요구사항
       1. 옵션 그룹 이름만 수정.
            옵션 그룹 삭제.
            옵션 그룹 추가.

       2. 옵션 값 수정
            기존 옵션 값 이름이나 색상 코드 수정.
            옵션 값 추가.
            옵션 값 삭제.

      옵션 그룹 업데이트 로직
        groupId가 없는 경우: 새로운 옵션 그룹 생성.
        groupName 변경 요청: 기존 옵션 그룹의 이름 수정.
        요청 데이터에 없고 기존 DB에만 존재하는 옵션 그룹: 삭제

    옵션 값 업데이트 로직
        valueId가 없는 경우: 새로운 옵션 값 생성.
        valueName 또는 colorCode가 변경된 경우: 기존 옵션 값 수정.
        요청 데이터에 없고 기존 DB에만 존재하는 옵션 값: 삭제.
    */
    public void updateOptionGroups(Long productId, List<OptionGroupUpdateServiceRequest> requests) {
        // Product 조회
        Product product = getProductWithOptionGroups(productId);

        // 기존 OptionGroup 가져오기
        List<OptionGroup> existingGroups = product.getOptionGroups();

        // 새로운 그룹과 값 추적을 위한 리스트
        List<OptionGroup> newlyCreatedGroups = new ArrayList<>();
        List<OptionValue> newlyCreatedValues = new ArrayList<>();

        // 요청을 처리하여 OptionGroup 및 OptionValue 수정
        for (OptionGroupUpdateServiceRequest request : requests) {
            if (request.getGroupId() == null) {
                // 새로운 옵션 그룹 추가
                OptionGroup newGroup = OptionGroup.create(request.getGroupName(), product);

                // 옵션 값 추가
                if (request.getOptionValues() != null) {
                    for (OptionValueUpdateServiceRequest valueRequest : request.getOptionValues()) {
                        OptionValue newValue = OptionValue.create(newGroup, valueRequest.getValueName(), valueRequest.getColorCode());
                        newGroup.addOptionValue(newValue);
                        newlyCreatedValues.add(newValue); // 새로 추가된 옵션 값 기록
                    }
                }

                // Product와 연관 추가 및 저장
                product.addOptionGroup(newGroup);
                newlyCreatedGroups.add(newGroup); // 새로 추가된 그룹 기록
                optionGroupRepository.save(newGroup);
                continue;
            }

            // 기존 옵션 그룹 수정
            OptionGroup existingGroup = existingGroups.stream()
                    .filter(group -> group.getNo().equals(request.getGroupId()))
                    .findFirst()
                    .orElseThrow(() -> new OptionGroupNotFoundException(OPTION_GROUP_NOT_FOUND));

            // 그룹 이름 변경 (제공된 경우만 수행)
            if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
                existingGroup.updateName(request.getGroupName());
            }

            // 옵션 값 수정 요청이 없는 경우 옵션 그룹 이름만 변경하거나 유지
            if (request.getOptionValues() == null || request.getOptionValues().isEmpty()) {
                continue;
            }

            // 기존 OptionValue 처리
            List<OptionValue> existingValues = existingGroup.getOptionValues();

            for (OptionValueUpdateServiceRequest valueRequest : request.getOptionValues()) {
                if (valueRequest.getValueId() == null) {
                    // 새로운 옵션 값 추가
                    OptionValue newValue = OptionValue.create(existingGroup, valueRequest.getValueName(), valueRequest.getColorCode());
                    existingGroup.addOptionValue(newValue);
                    newlyCreatedValues.add(newValue); // 새로 추가된 옵션 값 기록
                } else {
                    // 기존 옵션 값 수정
                    OptionValue existingValue = existingValues.stream()
                            .filter(value -> value.getNo().equals(valueRequest.getValueId()))
                            .findFirst()
                            .orElseThrow(() -> new OptionValueNotFoundException(OPTION_VALUE_NOT_FOUND));

                    existingValue.update(valueRequest.getValueName(), valueRequest.getColorCode());
                }
            }

            // 삭제 대상 옵션 값 제거 (새로 추가된 값은 제외)
            List<Long> requestedValueIds = request.getOptionValues().stream()
                    .map(OptionValueUpdateServiceRequest::getValueId)
                    .filter(Objects::nonNull) // null 값 제거
                    .toList();

            List<OptionValue> valuesToDelete = existingValues.stream()
                    .filter(value -> !requestedValueIds.contains(value.getNo())) // 요청 데이터에 없는 값
                    .filter(value -> !newlyCreatedValues.contains(value)) // 새로 생성된 값 제외
                    .toList();

            for (OptionValue valueToDelete : valuesToDelete) {
                deleteOptionValue(valueToDelete.getNo());
            }
        }

        // 요청된 그룹 ID (새로 생성된 그룹 제외)
        List<Long> requestedGroupIds = requests.stream()
                .map(OptionGroupUpdateServiceRequest::getGroupId)
                .filter(Objects::nonNull) // null 값 제거
                .toList();

        // 기존 그룹 중 삭제 대상 확인 (새로 생성된 그룹 제외)
        List<OptionGroup> groupsToDelete = existingGroups.stream()
                .filter(group -> !requestedGroupIds.contains(group.getNo())) // 요청 데이터에 없는 기존 그룹
                .filter(group -> !newlyCreatedGroups.contains(group)) // 새로 생성된 그룹 제외
                .toList();

        // 삭제 처리
        for (OptionGroup groupToDelete : groupsToDelete) {
            deleteOptionGroup(groupToDelete.getNo());
            product.removeOptionGroup(groupToDelete);
        }
    }

    private Product getProductWithOptionGroups(Long productId) {
        // Product 조회
        return productRepository.findByIdWithOptionGroups(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public OptionGroup getOptionGroup(Long optionGroupId) {
        return optionGroupRepository.findById(optionGroupId)
                .orElseThrow(() -> new OptionGroupNotFoundException(OPTION_GROUP_NOT_FOUND));
    }

    public OptionGroupResponse addOptionValue(Long optionGroupId, OptionValueCreateServiceRequest optionValueRequest) {
        OptionGroup optionGroup = getOptionGroup(optionGroupId);

        OptionValue optionValue = OptionValue.create(optionGroup, optionValueRequest.getValueName(), optionValueRequest.getColorCode());

        // OptionGroup에 새로운 OptionValue 추가 및 중복 검사
        optionGroup.addOptionValue(optionValue);

        return OptionGroupResponse.of(optionGroupRepository.save(optionGroup));
    }

    public void deleteOptionGroup(Long optionGroupId) {
        OptionGroup optionGroup = optionGroupRepository.findById(optionGroupId)
                .orElseThrow(() -> new OptionGroupNotFoundException(OPTION_GROUP_NOT_FOUND));

        optionGroupRepository.delete(optionGroup);
    }

    public void deleteOptionValue(Long optionValueId) {
        OptionValue optionValue = optionValueRepository.findOptionWithGroupById(optionValueId)
                .orElseThrow(() -> new OptionValueNotFoundException(OPTION_VALUE_NOT_FOUND));

        optionValue.getOptionGroup().removeOptionValue(optionValue);
        optionValueRepository.delete(optionValue);
    }
}

