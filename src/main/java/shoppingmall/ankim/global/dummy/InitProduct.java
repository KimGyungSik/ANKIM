package shoppingmall.ankim.global.dummy;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.query.helper.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class InitProduct {
    private final InitProductService initProductService;

    @PostConstruct
    public void init() {
        initProductService.init();
    }
    @Component
    static class InitProductService {
        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            int productCountPerSubCategory = 40; // 소분류당 40개씩 상품 생성

            // 중분류 카테고리(최상위) 생성
            Map<Condition, List<Category>> conditionToSubCategoryMap = new HashMap<>();
            for (Condition condition : Condition.values()) {
                if (condition.isCategoryCondition()) {
                    String categoryName = condition.getCategoryName();

                    List<String> subCategoryNames = switch (categoryName) {
                        case "BOTTOM" -> List.of("데님", "팬츠", "슬랙스", "쇼츠", "트레이닝팬츠", "스판혼방");
                        case "KNIT" -> List.of("니트", "가디건", "베스트");
                        case "OUTER" -> List.of("코트", "자켓", "가디건", "점퍼");
                        case "TOP" -> List.of("티셔츠", "맨투맨/후드", "슬리브리스");
                        case "SHIRT" -> List.of("셔츠", "블라우스");
                        case "OPS/SK" -> List.of("원피스", "미니스커트", "미디-롱 스커트");
                        default -> List.of();
                    };

                    // 중분류 및 소분류 생성
                    List<Category> subCategories = createCategory(em, categoryName, subCategoryNames);
                    conditionToSubCategoryMap.put(condition, subCategories);
                }
            }

            // 더미 데이터 생성 (소분류에 매핑)
            for (Map.Entry<Condition, List<Category>> entry : conditionToSubCategoryMap.entrySet()) {
                Condition condition = entry.getKey();
                List<Category> subCategories = entry.getValue();

                for (Category subCategory : subCategories) {
                    for (int i = 0; i < productCountPerSubCategory; i++) {
                        createProduct(em, condition, subCategory, i);
                    }
                }
            }

            System.out.println("각 소분류에 40개씩 총 " + (productCountPerSubCategory * conditionToSubCategoryMap.values().stream().mapToInt(List::size).sum()) + "개의 더미 상품이 생성되었습니다.");
        }

        // 상품 생성
        private void createProduct(EntityManager em, Condition condition, Category subCategory, int index) {
            OrderBy orderBy = getRandomOrderBy(index);
            List<InfoSearch> infoSearches = getRandomInfoSearches(index);
            List<ColorCondition> colorConditions = getRandomColorConditions(index);
            PriceCondition priceCondition = getRandomPriceCondition(index);
            Integer customMinPrice = priceCondition == PriceCondition.CUSTOM ? 10000 : null;
            Integer customMaxPrice = priceCondition == PriceCondition.CUSTOM ? 50000 : null;

            // 상품 생성
            Product product = Product.builder()
                    .category(subCategory) // ⭐ 소분류에 상품을 할당
                    .name(subCategory.getName() + " 상품 " + index)
                    .desc("테스트용 더미 상품입니다.")
                    .origPrice(10000 + (index * 1000))
                    .discRate(condition == Condition.DISCOUNT ? 20 : 0)
                    .qty(infoSearches.contains(InfoSearch.EXCLUDE_OUT_OF_STOCK) ? 50 : 0)
                    .rvwCnt(orderBy == OrderBy.HIGH_REVIEW ? 50 : 10)
                    .viewCnt(orderBy == OrderBy.HIGH_VIEW ? 500 : 100)
                    .wishCnt(condition == Condition.BEST ? 50 : 10)
                    .freeShip(infoSearches.contains(InfoSearch.FREESHIP) ? "Y" : "N")
                    .handMadeYn(condition == Condition.HANDMADE ? "Y" : "N")
                    .sellingStatus(ProductSellingStatus.SELLING)
                    .shipFee(2000)
                    .build();
            product.setCreatedAt(LocalDateTime.now().minusDays(index));
            em.persist(product);

            // 옵션 그룹 생성
            OptionGroup colorGroup = createOptionGroup(em, "컬러", product);
            OptionGroup sizeGroup = createOptionGroup(em, "사이즈", product);

            // 옵션 값 생성
            for (ColorCondition colorCondition : colorConditions) {
                createOptionValue(em, colorCondition.name(), colorCondition.getHexCode(), colorGroup);
            }
            createOptionValue(em, "M", null, sizeGroup);
            createOptionValue(em, "L", null, sizeGroup);
            product.updateSearchKeywords();

            // 상품 이미지 생성
            addProductImages(em, product, index);

            // 품목 생성
            createItem(em, "색상: " + colorConditions.get(0).name() + ", 사이즈: M", List.of(
                    colorGroup.getOptionValues().get(0),
                    sizeGroup.getOptionValues().get(0)
            ), product, index);

            createItem(em, "색상: " + colorConditions.get(0).name() + ", 사이즈: L", List.of(
                    colorGroup.getOptionValues().get(0),
                    sizeGroup.getOptionValues().get(1)
            ), product, index);
        }

        public static Member createMember(EntityManager entityManager, String loginId) {
            Member member = Member.builder()
                    .loginId(loginId)
                    .pwd("password")
                    .name("홍길동")
                    .phoneNum("010-1234-5678")
                    .birth(LocalDate.of(1990, 1, 1))
                    .grade(50)
                    .gender("M")
                    .joinDate(LocalDateTime.now())
                    .status(MemberStatus.ACTIVE)
                    .build();
            entityManager.persist(member);
            return member;
        }

        // Order 생성
        private void createOrderWithProduct(Product product, Member member) {
            String orderCode = "ORD20241125-1234567";
            // Item 추출
            List<Item> items = product.getItems();
            if (items.size() < 2) return; // 최소 2개 품목 필요

            Item item1 = items.get(0);
            Item item2 = items.get(1);

            // OrderItem 생성
            OrderItem orderItem1 = OrderItem.create(item1, 2); // 수량 2
            OrderItem orderItem2 = OrderItem.create(item2, 3); // 수량 3

            // Order 생성
            Order order = Order.create(
                    List.of(orderItem1, orderItem2),
                    member,
                    null,
                    LocalDateTime.now()
            );
            order.setOrdCode(orderCode);

            em.persist(order);

            // Cart 생성
            Cart cart = Cart.create(member, LocalDateTime.now());

            createCartItem(em, cart, member,product,item1,2);
            createCartItem(em, cart, member,product,item2,3);

            em.persist(cart);
        }

        public static void createCartItem(EntityManager entityManager, Cart cart, Member member,Product product,Item item,Integer qty) {
            // CartItem 생성
            CartItem cartItem = CartItem.create(cart, product, item, qty, LocalDateTime.now());
            cart.addCartItem(cartItem);
        }

        // Delivery 생성
//        private Delivery createDelivery(Member member) {
//            MemberAddress address = MemberAddress.create(
//                    member,
//                    "집",
//                    BaseAddress.builder()
//                            .zipCode(12345)
//                            .addressMain("서울시 강남구 테헤란로 123")
//                            .addressDetail("1층")
//                            .build(),
//                    "010-1234-5678",
//                    "010-5678-1234",
//                    "Y"
//            );
//            em.persist(address);
//
//            Delivery delivery = Delivery.create(
//                    address,
//                    "FastCourier",
//                    "문 앞에 놓아주세요.",
//                    () -> "TRACK123456" // TrackingNumberGenerator 구현
//            );
//            em.persist(delivery);
//
//            return delivery;
//        }


        // 카테고리 생성 메서드 (중분류 + 소분류 포함)
        private List<Category> createCategory(EntityManager em, String name, List<String> subCategoryNames) {
            Category parentCategory = Category.builder()
                    .name(name)
                    .build();
            em.persist(parentCategory);

            List<Category> subCategories = new ArrayList<>();
            for (String subCategoryName : subCategoryNames) {
                Category subCategory = Category.builder()
                        .name(subCategoryName)
                        .build();
                parentCategory.addSubCategory(subCategory);
                em.persist(subCategory);
                subCategories.add(subCategory);
            }

            return subCategories;
        }


        // 옵션 그룹 생성
        private OptionGroup createOptionGroup(EntityManager em, String name, Product product) {
            OptionGroup optionGroup = OptionGroup.builder()
                    .name(name)
                    .product(product)
                    .build();
            product.addOptionGroup(optionGroup);
            em.persist(optionGroup);
            return optionGroup;
        }

        // 옵션 값 생성
        private void createOptionValue(EntityManager em, String name, String colorCode, OptionGroup optionGroup) {
            OptionValue optionValue = OptionValue.builder()
                    .name(name)
                    .colorCode(colorCode)
                    .optionGroup(optionGroup)
                    .build();
            optionGroup.addOptionValue(optionValue);
            em.persist(optionValue);
        }

        // 상품 이미지 생성
        private void addProductImages(EntityManager em, Product product, int index) {
            ProductImg thumbnailImg = ProductImg.builder()
                    .imgName("thumbnail_" + index + ".jpg")
                    .oriImgName("썸네일 이미지 " + index)
                    .imgUrl("http://example.com/images/thumbnail_" + index + ".jpg")
                    .repimgYn("Y") // 대표 이미지
                    .ord(1)
                    .product(product)
                    .build();
            em.persist(thumbnailImg);

            ProductImg detailImg = ProductImg.builder()
                    .imgName("detail_" + index + ".jpg")
                    .oriImgName("상세 이미지 " + index)
                    .imgUrl("http://example.com/images/detail_" + index + ".jpg")
                    .repimgYn("N")
                    .ord(2)
                    .product(product)
                    .build();
            em.persist(detailImg);

            product.addProductImg(thumbnailImg);
            product.addProductImg(detailImg);
        }

        // 품목 생성
        private void createItem(EntityManager em, String name, List<OptionValue> optionValues, Product product, int index) {
            Item item = Item.builder()
                    .name(name)
                    .optionValues(optionValues)
                    .code("ITEM_" + index)
                    .addPrice(0)
                    .qty(50)
                    .safQty(10)
                    .maxQty(5)
                    .minQty(1)
                    .product(product)
                    .build();
            em.persist(item);
            product.addItem(item);
        }

        // 랜덤 Condition 생성
        private Condition getRandomCondition(int index) {
            return Condition.values()[index % Condition.values().length];
        }

        // 랜덤 OrderBy 생성
        private OrderBy getRandomOrderBy(int index) {
            return OrderBy.values()[index % OrderBy.values().length];
        }

        // 랜덤 InfoSearch 생성
        private List<InfoSearch> getRandomInfoSearches(int index) {
            return List.of(InfoSearch.values()[index % InfoSearch.values().length]);
        }

        // 랜덤 ColorCondition 생성
        private List<ColorCondition> getRandomColorConditions(int index) {
            return List.of(ColorCondition.values()[index % ColorCondition.values().length]);
        }

        // 랜덤 PriceCondition 생성
        private PriceCondition getRandomPriceCondition(int index) {
            return PriceCondition.values()[index % PriceCondition.values().length];
        }
    }

}
