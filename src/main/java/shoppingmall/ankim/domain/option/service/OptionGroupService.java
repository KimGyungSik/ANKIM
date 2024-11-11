package shoppingmall.ankim.domain.option.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionGroupService {

    private final OptionGroupRepository optionGroupRepository;
    private final ProductRepository productRepository;

//    public OptionGroup createOptionGroup(Long productId, OptionGroupCreateRequest request) {
        // Product 찾기
//        Product product = productRepository.findById(productId);
//                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // OptionValue 목록 생성
//        List<OptionValue> optionValues = request.getOptionValues().stream()
//                .map(optValReq -> OptionValue.create(null, optValReq.getValueName(), optValReq.getColorCode()))
//                .collect(Collectors.toList());

        // OptionGroup 생성
//        OptionGroup optionGroup = OptionGroup.create(request.getGroupName(), product, optionValues);

        // OptionGroup 저장
//        return optionGroupRepository.save(optionGroup);
//    }

    public OptionGroup getOptionGroup(Long optionGroupId) {
        return optionGroupRepository.findById(optionGroupId)
                .orElseThrow(() -> new OptionGroupNotFoundException(OPTION_GROUP_NOT_FOUND));
    }

    public OptionGroup addOptionValue(Long optionGroupId, OptionValueCreateRequest optionValueRequest) {
        OptionGroup optionGroup = getOptionGroup(optionGroupId);

        OptionValue optionValue = OptionValue.create(optionGroup, optionValueRequest.getValueName(), optionValueRequest.getColorCode());

        // OptionGroup에 새로운 OptionValue 추가 및 중복 검사
        optionGroup.addOptionValue(optionValue);

        return optionGroupRepository.save(optionGroup);
    }

    public void deleteOptionGroup(Long optionGroupId) {
        OptionGroup optionGroup = optionGroupRepository.findById(optionGroupId)
                .orElseThrow(() -> new OptionGroupNotFoundException(OPTION_GROUP_NOT_FOUND));

        optionGroupRepository.delete(optionGroup);
    }
}

