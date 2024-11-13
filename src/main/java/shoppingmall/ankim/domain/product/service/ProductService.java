package shoppingmall.ankim.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.service.ProductImgService;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.service.OptionGroupService;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.service.request.ProductCreateServiceRequest;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImgService productImgService;
    private final OptionGroupService optionGroupService;
    private final ItemRepository itemRepository;

    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        // 1. 카테고리 id로 카테고리 엔티티 가져오기
        Category category = categoryRepository.findById(request.getCategoryNo())
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        // 2. 상품 생성
        Product product = Product.create(
                category,
                request.getName(),
                request.getCode(),
                request.getDesc(),
                request.getDiscRate(),
                request.getSellPrice(),
                request.getOrigPrice(),
                request.getOptYn(),
                request.getRestockYn(),
                request.getQty(),
                request.getBestYn(),
                request.getFreeShip(),
                request.getShipFee(),
                request.getSearchKeywords(),
                request.getRelProdCode(),
                request.getCauProd(),
                request.getCauOrd(),
                request.getCauShip()
        );
        Product savedProduct = productRepository.save(product); // Product 먼저 저장

        // 3. 상품 이미지 생성
        productImgService.createProductImgs(savedProduct, request.getProductImages());

        // 4. 옵션 그룹 생성
        if (request.getOptionGroups() != null && !request.getOptionGroups().isEmpty()) {
            optionGroupService.createOptionGroups(savedProduct, request.getOptionGroups());
        }

        // 5. 품목 생성
//        itemService.createItemsForProduct(product, request);

        return ProductResponse.of(savedProduct);
    }


}
