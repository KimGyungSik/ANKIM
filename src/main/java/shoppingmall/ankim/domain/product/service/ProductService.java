package shoppingmall.ankim.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.service.ProductImgService;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.service.OptionGroupService;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.exception.CannotModifySellingProductException;
import shoppingmall.ankim.domain.product.exception.ProductNotFoundException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.service.request.CategoryRequest;
import shoppingmall.ankim.domain.product.service.request.ProductCreateServiceRequest;
import shoppingmall.ankim.domain.product.service.request.ProductUpdateServiceRequest;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

import static shoppingmall.ankim.domain.product.entity.ProductSellingStatus.*;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImgService productImgService;
    private final OptionGroupService optionGroupService;
    private final ItemService itemService;
    /*
        1. 카테고리 검증 및 조회
        2. 상품 생성 및 저장
        3. 상품 이미지 저장
        4. 옵션 그룹 및 옵션 값 생성
        5. 옵션 조합 기반 품목 생성
     */
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        // 1. 카테고리 id로 카테고리 엔티티 가져오기
        Category category = getCategory(request);

        // 2. 상품 생성
        Product savedProduct = getProduct(request, category);

        // 3. 상품 이미지 생성
        productImgService.createProductImgs(savedProduct.getNo(), request.getProductImages());

        // 4. 옵션 그룹 생성 및 저장
        saveOptionGroup(request, savedProduct);

        // 5. 품목 생성
        itemService.createItems(savedProduct.getNo(), request.getItems());

        return ProductResponse.of(savedProduct);
    }


    // 상품 수정
    public ProductResponse updateProduct(Long productId, ProductUpdateServiceRequest request) {
        // 1. 상품 id로 상품 엔티티 가져와서 수정하기
        Product modifyProduct = getProduct(productId);
        // 1-1. 판매중인 상품은 카테고리 & 옵션 및 재고 수정 X 예외 발생
        if(modifyProduct.getSellingStatus() == SELLING) {
            throw new CannotModifySellingProductException(CANNOT_MODIFY_SELLING_PRODUCT);
        }
        modifyProduct.change(request);

        // 2. 카테고리 id로 카테고리 엔티티 가져오기
        Category category = getCategory(request);
        modifyProduct.changeCategory(category);

        // 3. 상품 이미지 수정 ( 기존 이미지는 유지, 요청에 없는 이미지는 삭제, 요청에 새로운 이미지는 추가)
        productImgService.updateProductImgs(productId, request.getProductImages());

        // 4. 옵션 그룹 및 옵션 값 수정 (옵션은 식별자 존재유무로 기존 옵션인 경우엔 업데이트, 새로운 옵션인 경우엔 새로 생성)
        optionGroupService.updateOptionGroups(productId, request.getOptionGroups());

        // 5. 품목 수정 ( 기존 품목은 유지 및 업데이트, 요청에 없는 품목은 삭제, 요청에 새로운 품목은 추가)
        itemService.updateItems(productId, request.getItems());

        return ProductResponse.of(modifyProduct);
    }


    private void saveOptionGroup(ProductCreateServiceRequest request, Product savedProduct) {
        if (request.getOptionGroups() != null && !request.getOptionGroups().isEmpty()) {
            List<OptionGroupResponse> optionGroups = optionGroupService.createOptionGroups(savedProduct.getNo(), request.getOptionGroups());
        }
    }

    private Product getProduct(ProductCreateServiceRequest request, Category category) {
        // 상품 생성 시 판매가는 원가와 할인율을 적용하여 세팅됨
        Product product = Product.create(
                category,
                request.getName(),
                request.getCode(),
                request.getDesc(),
                request.getDiscRate(),
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
        return productRepository.save(product);
    }

    private Category getCategory(CategoryRequest request) {
        return categoryRepository.findById(request.getCategoryNo())
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }
}
