package shoppingmall.ankim.domain.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.item.dto.ItemPreviewResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.OptionGroupService;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.PRODUCT_NOT_FOUND;
// 옵션 조합 생성 -> 조합 결과 반환 -> 세부 값 입력 및 저장
// 옵션 조합 생성 후 품목 반환 → 각 품목에 대한 세부 값 입력 및 저장
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;

    /**
     * 옵션 조합 생성 및 미리보기 반환
     */
    public List<ItemPreviewResponse> generateOptionCombinations(List<OptionGroupCreateServiceRequest> optionGroupRequests) {
        // 옵션 그룹의 옵션 값 리스트를 추출
        List<List<OptionValueCreateServiceRequest>> optionValueGroups = optionGroupRequests.stream()
                .map(OptionGroupCreateServiceRequest::getOptionValues)
                .toList();

        // 옵션 값 조합 생성
        List<List<OptionValueCreateServiceRequest>> combinations = new ArrayList<>();
        createCombinations(optionValueGroups, 0, new ArrayList<>(), combinations);

        // 조합 결과를 DTO로 변환하여 반환
        return combinations.stream()
                .map(combination -> toPreviewResponse(combination, optionGroupRequests))
                .toList();
    }

    /**
     * 품목 등록
     */
    public List<ItemResponse> createItems(Product product, ItemCreateServiceRequest request) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (ItemDetailServiceRequest detail : request.getItems()) {

            // 1. 옵션 값 이름으로 OptionValue 조회
            List<OptionValue> optionValues = getOptionValuesFromNames(detail.getOptionValueNames(), product);

            // 2. 품목 생성
            Item item = Item.create(
                    product,
                    optionValues,
                    generateItemCode(product), // 품목 코드 생성
                    detail.getName(), // 품목명
                    detail.getAddPrice(),
                    detail.getQty(),
                    detail.getSafQty(),
                    detail.getMaxQty(),
                    detail.getMinQty()
            );

            // 3. 품목 저장
            Item savedItem = itemRepository.save(item);
            product.addItem(savedItem);
            itemResponses.add(ItemResponse.of(savedItem));
        }
        return itemResponses;
    }

    /**
     * 품목 수정
     */








    private List<OptionValue> getOptionValuesFromNames(List<String> optionValueNames, Product product) {
        List<OptionGroup> optionGroups = optionGroupRepository.findAllByProduct(product);

        return optionGroups.stream()
                .flatMap(optionGroup -> optionValueRepository.findByOptionGroupNoAndNameIn(
                        optionGroup.getNo(),
                        optionValueNames
                ).stream())
                .collect(Collectors.toList());
    }

    private String generateItemCode(Product product) {
        // 기존 품목의 갯수를 기반으로 새로운 코드 생성
        long itemCount = itemRepository.countByProduct(product);
        return product.getCode() + "-" + (itemCount + 1);
    }


    /**
     * 옵션 값 조합 생성
     */
    private void createCombinations(List<List<OptionValueCreateServiceRequest>> groups, int depth,
                                    List<OptionValueCreateServiceRequest> current,
                                    List<List<OptionValueCreateServiceRequest>> result) {
        if (depth == groups.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (OptionValueCreateServiceRequest value : groups.get(depth)) {
            current.add(value);
            createCombinations(groups, depth + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * 조합 결과를 미리보기 DTO로 변환
     */
    private ItemPreviewResponse toPreviewResponse(List<OptionValueCreateServiceRequest> combination,
                                                  List<OptionGroupCreateServiceRequest> optionGroups) {
        // 조합 이름 생성 (옵션 그룹 이름과 옵션 값 이름을 포함)
        String name = combination.stream()
                .map(opt -> {
                    // 해당 옵션 값이 속한 그룹의 이름을 찾음
                    String groupName = optionGroups.stream()
                            .filter(group -> group.getOptionValues().contains(opt))
                            .findFirst()
                            .map(OptionGroupCreateServiceRequest::getGroupName)
                            .orElse("Unknown");
                    return groupName + ": " + opt.getValueName();
                })
                .collect(Collectors.joining(", "));

        // 조합 결과를 DTO로 변환
        return ItemPreviewResponse.builder()
                .name(name)
                .optionValueNames(combination.stream()
                        .map(OptionValueCreateServiceRequest::getValueName)
                        .toList())
                .build();
    }
}
