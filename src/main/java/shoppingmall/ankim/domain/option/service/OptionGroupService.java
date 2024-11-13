package shoppingmall.ankim.domain.option.service;

import lombok.RequiredArgsConstructor;
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
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionGroupService {

    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;

    public List<OptionGroupResponse> createOptionGroups(Product product, List<OptionGroupCreateServiceRequest> requests) {
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
    }
}

