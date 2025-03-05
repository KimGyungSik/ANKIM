package shoppingmall.ankim.domain.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.item.dto.ItemPreviewResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.config.lock.NamedLock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.ITEM_NOT_FOUND;
import static shoppingmall.ankim.global.exception.ErrorCode.PRODUCT_NOT_FOUND;

// 옵션 조합 생성 -> 조합 결과 반환 -> 세부 값 입력 및 저장
// 옵션 조합 생성 후 품목 반환 → 각 품목에 대한 세부 값 입력 및 저장
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;

    // 재고 차감
    public synchronized void reduceStockWithSynchronized(Long itemNo, Integer quantity) {
        Item item = itemRepository.findByNo(itemNo)
                .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
        item.deductQuantity(quantity);
        itemRepository.saveAndFlush(item);
    }

    // 재고 복구
    public synchronized void restoreStockWithSynchronized(Long itemNo, Integer quantity) {
        Item item = itemRepository.findByNo(itemNo)
                .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
        item.restoreQuantity(quantity);
        itemRepository.saveAndFlush(item);
    }

    // 재고 차감
    @NamedLock(key = "'LOCK_' + #itemNo", timeout = 30)
    public void reduceStock(Long itemNo, Integer quantity) {
        Item item = itemRepository.findByNo(itemNo)
                .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
        item.deductQuantity(quantity);
    }

    // 재고 복구
    @NamedLock(key = "'LOCK_' + #itemNo", timeout = 30)
    public void restoreStock(Long itemNo, Integer quantity) {
        Item item = itemRepository.findByNo(itemNo)
                .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
        item.restoreQuantity(quantity);
    }

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
    public List<ItemResponse> createItems(Long productId, ItemCreateServiceRequest request) {
        Product product = getProduct(productId);

        List<ItemResponse> orderItemRespons = new ArrayList<>();
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
            orderItemRespons.add(ItemResponse.of(savedItem));
        }
        return orderItemRespons;
    }

    /**
     * 품목 수정
     * 옵션 수정을 같이? 품목 수정 시 옵션 수정도 같이 진행
     * 그냥 품목 아이디로 품목 엔티티 가져오기
     * 수정한 옵션그룹 DTO를 받아서 해당 ID가 ItemOption테이블에 존재하는지 확인
     * 존재하면 기존 품목 업데이트
     * 존재하지 않으면 새로운 품목 생성

     * 옵션아이템 테이블에서 옵션 ID 로 품목을 꺼내오기
     * 해당 옵션 ID가 존재하면 기존 품목 업데이트
     * 존재하지 않으면 새로운 품목 생성

     * 기존 품목 업데이트와 새로 생성되어야 하는 품목을 구별해야함
     * 품목명이 다르면 품목 새로 생성
     * 품목명이 같다면 업데이트만 진행

     * 조건 1. 품목 상태가 판매중이면 안됨



     * TODO OptionValueResponse에 itemId가 있으니 해당 필드로 기존 품목 유무로 판단해보는것도 고려해볼것
     * TODO ItemDetailServiceRequest에 OptionValueRequest(itemId 필드존재)를 받아서 판단
     * TODO 근거 -> 관리자 상품 상세 페이지에 OptionValueResponse(itemId 필드존재)를 반환하기 때문에
     */
    public void updateItems(Long productId, ItemUpdateServiceRequest updatedItemsRequest) {
        List<ItemDetailServiceRequest> updatedItems = updatedItemsRequest.getItems();

        // 1. 상품 ID로 기존 품목 조회
        Product product = getProduct(productId);

        List<Item> existingItems = itemRepository.findByProduct_No(productId);

        // 2. 수정 요청 데이터를 Map 형태로 변환 (key: name, value: ItemDetailServiceRequest)
        Map<String, ItemDetailServiceRequest> updatedItemMap = updatedItems.stream()
                .collect(Collectors.toMap(ItemDetailServiceRequest::getName, item -> item));

        // 3. 업데이트 및 생성 리스트 정의
        List<Item> itemsToSave = new ArrayList<>();
        List<Item> itemsToDelete = new ArrayList<>(existingItems); // 초기값은 기존 리스트 전체

        for (Item item : existingItems) {
            if (updatedItemMap.containsKey(item.getName())) {
                // 기존 품목 업데이트
                ItemDetailServiceRequest detail = updatedItemMap.get(item.getName());
                item.change(detail);
                itemsToDelete.remove(item); // 업데이트 된 품목은 삭제 리스트에서 제외
            }
        }

        // 새로 추가해야 할 품목 찾기
        for (ItemDetailServiceRequest detail : updatedItems) {
            if (existingItems.stream().noneMatch(item -> item.getName().equals(detail.getName()))) {
                // 새 품목 생성
                Item newItem = Item.create(
                        product,
                        getOptionValuesFromNames(detail.getOptionValueNames(), product), // 옵션 값 매핑
                        generateItemCode(product), // 새로운 품목 코드 생성
                        detail.getName(),
                        detail.getAddPrice(),
                        detail.getQty(),
                        detail.getSafQty(),
                        detail.getMaxQty(),
                        detail.getMinQty()
                );
                itemsToSave.add(newItem);
            }
        }

        // 4. 삭제해야 할 품목 처리
        itemRepository.deleteAll(itemsToDelete);

        // 5. 저장해야 할 품목 저장
        itemRepository.saveAll(itemsToSave);
    }


    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }
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
