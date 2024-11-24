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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProductFactory {

    // 1. 특정 조건별 상품 생성 (NEW, BEST, HANDMADE, DISCOUNT 등)
    public static void createTestProductsWithSubcategories(
            EntityManager entityManager,
            int newCount, int bestCount, int handmadeCount, int discountCount,
            Map<String, List<String>> categoryStructure) {
        LocalDateTime now = LocalDateTime.now();

        // 1. NEW 키워드가 포함된 상품 생성
        Product productWithNewName = Product.builder()
                .name("NEW Fashionable Shirt")
                .desc("Comfortable and trendy NEW shirt")
                .searchKeywords("Fashion, Shirt")
                .origPrice(15000)
                .discRate(0) // 할인 없음
                .rvwCnt(10)  // 리뷰 수 추가
                .viewCnt(150) // 조회수 추가
                .build();
        productWithNewName.setCreatedAt(now.minusDays(5));
        entityManager.persist(productWithNewName);
        addProductImages(entityManager, productWithNewName, 1);

        // 2. 상품 색상 키워드 포함 (RED)
        Product productWithRedKeyword = Product.builder()
                .name("Summer Dress")
                .desc("Bright and elegant dress")
                .searchKeywords("RED, Dress")
                .discRate(0) // 할인 없음
                .origPrice(20000)
                .rvwCnt(5)
                .viewCnt(100)
                .build();
        productWithRedKeyword.setCreatedAt(now.minusDays(10));
        entityManager.persist(productWithRedKeyword);
        addProductImages(entityManager, productWithRedKeyword, 2);

        // 3. 상품 상세 설명 키워드 포함
        Product productWithDescKeyword = Product.builder()
                .name("Comfortable Cotton T-shirt")
                .desc("Made of 100% pure '코튼재질' for maximum comfort")
                .searchKeywords("Cotton, T-shirt")
                .discRate(0) // 할인 없음
                .origPrice(10000)
                .rvwCnt(15)
                .viewCnt(200)
                .build();
        productWithDescKeyword.setCreatedAt(now.minusDays(15));
        entityManager.persist(productWithDescKeyword);
        addProductImages(entityManager, productWithDescKeyword, 3);

        // 중분류 -> 소분류 구조를 순회하면서 데이터 생성
        for (Map.Entry<String, List<String>> entry : categoryStructure.entrySet()) {
            String middleCategory = entry.getKey(); // 중분류
            List<String> subcategories = entry.getValue(); // 소분류 목록

            // 중분류 생성
            Category middleCategoryEntity = Category.builder()
                    .name(middleCategory)
                    .build();
            entityManager.persist(middleCategoryEntity);

            for (String subcategory : subcategories) {
                // 소분류 생성
                Category subCategoryEntity = Category.builder()
                        .name(subcategory)
                        .build();
                middleCategoryEntity.addSubCategory(subCategoryEntity);
                entityManager.persist(subCategoryEntity);

                // NEW 상품 생성
                for (int i = 0; i < newCount; i++) {
                    Product newProduct = Product.builder()
                            .category(subCategoryEntity)
                            .name("NEW 상품 - " + subcategory + " - " + i)
                            .desc("NEW 상품 설명")
                            .searchKeywords("NEW")
                            .discRate(0) // 할인 없음
                            .origPrice(10000 + i)
                            .rvwCnt(i) // 리뷰 수 추가
                            .viewCnt(i * 10) // 조회수 추가
                            .wishCnt(0)
                            .handMadeYn("N")
                            .build();
                    newProduct.setCreatedAt(now.minusDays(i));
                    entityManager.persist(newProduct);

                    // 이미지 추가 (옵션)
                    addProductImages(entityManager, newProduct, i);
                }

                // BEST 상품 생성
                for (int i = 0; i < bestCount; i++) {
                    Product bestProduct = Product.builder()
                            .category(subCategoryEntity)
                            .name("BEST 상품 - " + subcategory + " - " + i)
                            .desc("BEST 상품 설명")
                            .discRate(0)
                            .origPrice(15000 + i)
                            .rvwCnt(50 + i) // 리뷰 수 추가
                            .viewCnt(500 + i * 10) // 조회수 추가
                            .wishCnt(50 + i) // 찜 수 50 이상
                            .handMadeYn("N")
                            .build();
                    bestProduct.setCreatedAt(now.minusDays(30));
                    entityManager.persist(bestProduct);

                    // 이미지 추가 (옵션)
                    addProductImages(entityManager, bestProduct, i);
                }

                // HANDMADE 상품 생성
                for (int i = 0; i < handmadeCount; i++) {
                    Product handmadeProduct = Product.builder()
                            .category(subCategoryEntity)
                            .name("HANDMADE 상품 - " + subcategory + " - " + i)
                            .desc("HANDMADE 상품 설명")
                            .discRate(0)
                            .origPrice(20000 + i)
                            .rvwCnt(20 + i) // 리뷰 수 추가
                            .viewCnt(300 + i * 10) // 조회수 추가
                            .wishCnt(0)
                            .handMadeYn("Y") // 핸드메이드
                            .build();
                    handmadeProduct.setCreatedAt(now.minusDays(15));
                    entityManager.persist(handmadeProduct);

                    // 이미지 추가 (옵션)
                    addProductImages(entityManager, handmadeProduct, i);
                }

                // DISCOUNT 상품 생성
                for (int i = 0; i < discountCount; i++) {
                    Product discountProduct = Product.builder()
                            .category(subCategoryEntity)
                            .name("DISCOUNT 상품 - " + subcategory + " - " + i)
                            .desc("DISCOUNT 상품 설명")
                            .discRate(10 + i) // 할인율
                            .origPrice(25000 + i)
                            .rvwCnt(5 + i) // 리뷰 수 추가
                            .viewCnt(150 + i * 5) // 조회수 추가
                            .wishCnt(0)
                            .handMadeYn("N")
                            .build();
                    discountProduct.setCreatedAt(now.minusDays(10));
                    entityManager.persist(discountProduct);

                    // 이미지 추가 (옵션)
                    addProductImages(entityManager, discountProduct, i);
                }
            }
        }
        System.out.println("Test products successfully created.");
    }

    // 추가된 이미지 생성 메서드
    private static void addProductImages(EntityManager entityManager, Product product, int index) {
        // 대표 이미지 (썸네일)
        ProductImg thumbnailImg = ProductImg.builder()
                .imgName("thumbnail_" + index + ".jpg")
                .oriImgName("썸네일 이미지 " + index)
                .imgUrl("http://example.com/images/thumbnail_" + index + ".jpg")
                .repimgYn("Y") // 대표 이미지
                .ord(1) // 첫 번째 이미지
                .product(product)
                .build();
        entityManager.persist(thumbnailImg);

        ProductImg thumbnailImg2 = ProductImg.builder()
                .imgName("thumbnail_" + index + ".jpg")
                .oriImgName("썸네일 이미지2 " + index)
                .imgUrl("http://example.com/images/thumbnail_" + index + ".jpg")
                .repimgYn("Y") // 대표 이미지
                .ord(2) // 두 번째 이미지
                .product(product)
                .build();
        entityManager.persist(thumbnailImg2);

        product.addProductImg(thumbnailImg);
        product.addProductImg(thumbnailImg2);

        // 상세 이미지
        ProductImg detailImg = ProductImg.builder()
                .imgName("detail_" + index + ".jpg")
                .oriImgName("상세 이미지 " + index)
                .imgUrl("http://example.com/images/detail_" + index + ".jpg")
                .repimgYn("N") // 상세 이미지
                .ord(2) // 두 번째 이미지
                .product(product)
                .build();
        entityManager.persist(detailImg);

        product.addProductImg(detailImg);
    }


    // 2. 카테고리 생성 메서드
    private static Category createCategory(EntityManager entityManager, String name, String parentName) {
        Category parentCategory = null;
        if (parentName != null) {
            parentCategory = entityManager.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class)
                    .setParameter("name", parentName)
                    .getSingleResult();
        }

        Category category = Category.builder()
                .name(name)
                .build();
        if (parentCategory != null) {
            parentCategory.addSubCategory(category);
        }
        entityManager.persist(category);
        return category;
    }

    // 3. 상품 생성 메서드 (특정 속성으로 생성)
    private static Product createProductWithAttributes(EntityManager entityManager, String code, String name, Category category,
                                                       LocalDateTime createdAt, long wishCnt, int discRate,
                                                       boolean isHandmade, boolean isDiscounted) {
        // 상품 생성
        Product product = Product.builder()
                .category(category)
                .name(name)
                .code(code)
                .desc(name + " 상세 설명입니다.")
                .origPrice(100000)
                .discRate(discRate)
                .qty(100)
                .wishCnt((int) wishCnt)
                .handMadeYn(isHandmade ? "Y" : "N")
                .sellingStatus(ProductSellingStatus.SELLING)
                .build();
        product.setCreatedAt(createdAt);
        entityManager.persist(product);

        // 상품 이미지 추가
        ProductImg thumbnail = ProductImg.builder()
                .imgName("thumbnail.jpg")
                .oriImgName(name + " 썸네일")
                .imgUrl("http://example.com/images/" + code + "_thumbnail.jpg")
                .repimgYn("Y")
                .ord(1)
                .product(product)
                .build();
        entityManager.persist(thumbnail);

        // 옵션 그룹 생성
        OptionGroup colorGroup = OptionGroup.builder().name("컬러").product(product).build();
        OptionGroup sizeGroup = OptionGroup.builder().name("사이즈").product(product).build();
        entityManager.persist(colorGroup);
        entityManager.persist(sizeGroup);

        // 옵션 값 생성
        OptionValue black = OptionValue.builder().name("블랙").colorCode("#000000").optionGroup(colorGroup).build();
        OptionValue medium = OptionValue.builder().name("M").optionGroup(sizeGroup).build();
        entityManager.persist(black);
        entityManager.persist(medium);

        // 품목 추가
        Item item = Item.builder()
                .name(name + " 품목")
                .code(code + "_ITEM")
                .optionValues(List.of(black, medium))
                .addPrice(0)
                .qty(50)
                .safQty(10)
                .maxQty(5)
                .minQty(1)
                .product(product)
                .build();
        entityManager.persist(item);

        return product;
    }


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

