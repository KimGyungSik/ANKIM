package shoppingmall.ankim.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final OptionGroupRepository optionGroupRepository;

    public List<ItemResponse> createItem(Long productId, List<Long> optionGroupIds, ItemCreateServiceRequest request) {
        // Product 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));

        // OptionGroup 목록 조회
        List<OptionGroup> optionGroups = optionGroupRepository.findAllById(optionGroupIds);

        // 조합 생성 및 Item 생성 로직
        List<List<OptionValue>> optionCombinations = getOptionCombinations(optionGroups);
        List<ItemResponse> itemResponses = new ArrayList<>();

        for (int i = 0; i < optionCombinations.size(); i++) {
            List<OptionValue> optionCombination = optionCombinations.get(i);

            // `i + 1`을 이용하여 순차적으로 코드 생성
            String itemCode = product.getCode() + "-" + (i + 1);
            String itemName = optionCombination.stream()
                    .map(optionValue -> optionValue.getOptionGroup().getName() + ": " + optionValue.getName())
                    .collect(Collectors.joining(", "));

            Item item = Item.create(
                    product,
                    optionCombination,
                    itemCode,
                    itemName,
                    request.getAddPrice(),
                    request.getQty(),
                    request.getSafQty(),
                    request.getMaxQty(),
                    request.getMinQty()
            );

            Item savedItem = itemRepository.save(item);
            product.addItem(savedItem); // `product`에 직접 추가하지 않고 itemCode는 `i` 기반으로
            itemResponses.add(ItemResponse.of(savedItem));
        }

        return itemResponses;
    }

    private List<List<OptionValue>> getOptionCombinations(List<OptionGroup> optionGroups) {
        List<List<OptionValue>> combinations = new ArrayList<>();
        createCombinations(optionGroups, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private void createCombinations(List<OptionGroup> optionGroups, int depth, List<OptionValue> current, List<List<OptionValue>> result) {
        if (depth == optionGroups.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (OptionValue optionValue : optionGroups.get(depth).getOptionValues()) {
            current.add(optionValue);
            createCombinations(optionGroups, depth + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}





