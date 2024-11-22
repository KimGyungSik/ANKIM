package shoppingmall.ankim.factory;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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

    public static Product createProduct(EntityManager entityManager) {
        // 카테고리 생성 및 저장

        Category subCategory = Category.builder()
                .name("코트")
                .build();
        entityManager.persist(subCategory);

        Category category = Category.builder()
                .name("상의")
                .build();
        category.addSubCategory(subCategory);

        entityManager.persist(category);
        // 상품 생성 및 저장
        Product product = Product.builder()
                .category(subCategory)
                .name("캐시미어 코트")
                .desc("부드럽고 고급스러운 캐시미어 코트")
                .discRate(10)
                .origPrice(120000)
                .qty(100)
                .sellingStatus(ProductSellingStatus.SELLING)
                .build();
        entityManager.persist(product);

        // 옵션 그룹 생성 및 저장
        OptionGroup colorGroup = OptionGroup.builder()
                .name("컬러")
                .product(product)
                .build();
        OptionGroup sizeGroup = OptionGroup.builder()
                .name("사이즈")
                .product(product)
                .build();
        entityManager.persist(colorGroup);
        entityManager.persist(sizeGroup);

        product.addOptionGroup(colorGroup);
        product.addOptionGroup(sizeGroup);

        // 옵션 값 생성 및 저장
        OptionValue black = OptionValue.builder()
                .name("블랙")
                .colorCode("#000000")
                .optionGroup(colorGroup)
                .build();
        OptionValue gray = OptionValue.builder()
                .name("그레이")
                .colorCode("#808080")
                .optionGroup(colorGroup)
                .build();
        OptionValue medium = OptionValue.builder()
                .name("M")
                .optionGroup(sizeGroup)
                .build();
        OptionValue large = OptionValue.builder()
                .name("L")
                .optionGroup(sizeGroup)
                .build();
        entityManager.persist(black);
        entityManager.persist(gray);
        entityManager.persist(medium);
        entityManager.persist(large);

        colorGroup.addOptionValue(black);
        colorGroup.addOptionValue(gray);
        sizeGroup.addOptionValue(medium);
        sizeGroup.addOptionValue(large);

        // 상품 이미지 생성 및 저장
        ProductImg thumbnail = ProductImg.builder()
                .imgName("thumbnail.jpg")
                .oriImgName("캐시미어 코트 썸네일")
                .imgUrl("http://example.com/images/thumbnail.jpg")
                .repimgYn("Y")
                .ord(1)
                .product(product)
                .build();
        ProductImg detail = ProductImg.builder()
                .imgName("detail.jpg")
                .oriImgName("캐시미어 코트 상세")
                .imgUrl("http://example.com/images/detail.jpg")
                .repimgYn("N")
                .ord(2)
                .product(product)
                .build();
        entityManager.persist(thumbnail);
        entityManager.persist(detail);

        product.addProductImg(thumbnail);
        product.addProductImg(detail);

        // 품목 생성 및 저장
        Item item1 = Item.builder()
                .name("색상: 블랙, 사이즈: M")
                .optionValues(List.of(black, medium))
                .code("P001-BLK-M")
                .addPrice(0)
                .qty(50)
                .safQty(10)
                .maxQty(5)
                .minQty(1)
                .product(product)
                .build();
        Item item2 = Item.builder()
                .name("색상: 블랙, 사이즈: L")
                .optionValues(List.of(black, large))
                .code("P001-BLK-L")
                .addPrice(0)
                .qty(30)
                .safQty(5)
                .maxQty(3)
                .minQty(1)
                .product(product)
                .build();
        entityManager.persist(item1);
        entityManager.persist(item2);

        product.addItem(item1);
        product.addItem(item2);

        return product;
    }


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

