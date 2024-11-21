package shoppingmall.ankim.factory;

import shoppingmall.ankim.domain.product.entity.Product;

import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.List;

public class ProductFactory {

    public static Product createProduct(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            OptionGroupRepository optionGroupRepository,
            OptionValueRepository optionValueRepository,
            ProductImgRepository productImgRepository,
            ItemRepository itemRepository) {

        // 카테고리 생성 및 저장
        Category category = categoryRepository.save(
                Category.builder()
                        .name("상의")
                        .subCategories(List.of(Category.builder().name("코트").build()))
                        .build()
        );

        // 상품 생성 및 저장
        Product product = productRepository.save(
                Product.builder()
                        .category(category)
                        .name("캐시미어 코트")
                        .desc("부드럽고 고급스러운 캐시미어 코트")
                        .discRate(10)
                        .origPrice(120000)
                        .qty(100)
                        .sellingStatus(ProductSellingStatus.SELLING)
                        .build()
        );

        // 옵션 그룹 생성 및 저장
        OptionGroup colorGroup = optionGroupRepository.save(
                OptionGroup.builder()
                        .name("컬러")
                        .product(product)
                        .build()
        );
        OptionGroup sizeGroup = optionGroupRepository.save(
                OptionGroup.builder()
                        .name("사이즈")
                        .product(product)
                        .build()
        );

        product.addOptionGroup(colorGroup);
        product.addOptionGroup(sizeGroup);

        // 옵션 값 생성 및 저장
        List<OptionValue> optionValues = optionValueRepository.saveAll(List.of(
                OptionValue.builder().name("블랙").colorCode("#000000").optionGroup(colorGroup).build(),
                OptionValue.builder().name("그레이").colorCode("#808080").optionGroup(colorGroup).build(),
                OptionValue.builder().name("M").optionGroup(sizeGroup).build(),
                OptionValue.builder().name("L").optionGroup(sizeGroup).build()
        ));

        // 옵션 값들을 옵션 그룹에 추가
        for (OptionValue optionValue : optionValues) {
            if (optionValue.getOptionGroup().getName().equals("컬러")) {
                colorGroup.addOptionValue(optionValue);
            } else if (optionValue.getOptionGroup().getName().equals("사이즈")) {
                sizeGroup.addOptionValue(optionValue);
            }
        }

        // 상품 이미지 생성 및 저장
        List<ProductImg> productImgs = productImgRepository.saveAll(List.of(
                ProductImg.create("thumbnail.jpg", "캐시미어 코트 썸네일", "http://example.com/images/thumbnail.jpg", "Y", 1, product),
                ProductImg.create("detail.jpg", "캐시미어 코트 상세", "http://example.com/images/detail.jpg", "N", 2, product)
        ));

        for (ProductImg productImg : productImgs) {
            product.addProductImg(productImg);
        }

        // 품목 생성 및 저장
        List<Item> items = itemRepository.saveAll(List.of(
                Item.builder()
                        .name("색상: 블랙, 사이즈: M")
                        .optionValues(List.of(
                                optionValues.get(0), // 블랙
                                optionValues.get(2)  // M
                        ))
                        .code("P001-BLK-M")
                        .addPrice(0)
                        .qty(50)
                        .safQty(10)
                        .maxQty(5)
                        .minQty(1)
                        .product(product)
                        .build(),
                Item.builder()
                        .name("색상: 블랙, 사이즈: L")
                        .optionValues(List.of(
                                optionValues.get(0), // 블랙
                                optionValues.get(3)  // L
                        ))
                        .code("P001-BLK-L")
                        .addPrice(0)
                        .qty(30)
                        .safQty(5)
                        .maxQty(3)
                        .minQty(1)
                        .product(product)
                        .build()
        ));
        for (Item item : items) {
            product.addItem(item);
        }

        return product;
    }
}

