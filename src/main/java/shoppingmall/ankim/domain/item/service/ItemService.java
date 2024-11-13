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
import shoppingmall.ankim.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemResponse> createItem(Product product, List<OptionGroup> optionGroups, ItemCreateServiceRequest request) {
        List<ItemResponse> itemResponses = new ArrayList<>();

        // 모든 옵션 그룹의 옵션 값 조합 생성
        List<List<OptionValue>> optionCombinations = getOptionCombinations(optionGroups);

        // 각 조합에 대해 Item을 생성
        for (int i = 0; i < optionCombinations.size(); i++) {
            List<OptionValue> optionCombination = optionCombinations.get(i);

            // 1. 상품 코드에서 품목 수만큼 번호를 붙여서 고유 품목 코드를 생성
            String itemCode = product.getCode() + "-" + (i + 1); // 예: PROD123-1, PROD123-2 등

            // 2. 옵션 그룹과 옵션 값의 조합으로 품목명 생성
            String itemName = optionCombination.stream()
                    .map(optionValue -> optionValue.getOptionGroup().getName() + ": " + optionValue.getName())
                    .collect(Collectors.joining(", ")); // 예: "컬러: Blue, 사이즈: large"

            // 3. 품목 엔티티 생성 및 저장
            Item item = Item.create(
                    product,
                    optionCombination, // 옵션 값 조합 리스트 전달
                    itemCode,
                    itemName,
                    request.getAddPrice(),
                    request.getQty(),
                    request.getSafQty(),
                    request.getMaxQty(),
                    request.getMinQty()
            );

            Item savedItem = itemRepository.save(item); // 생성한 Item을 데이터베이스에 저장
            product.addItem(savedItem); // Product 엔티티에 품목 추가
            itemResponses.add(ItemResponse.of(savedItem)); // ItemResponse 리스트에 추가
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





