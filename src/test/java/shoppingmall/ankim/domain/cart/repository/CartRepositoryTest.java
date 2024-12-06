package shoppingmall.ankim.domain.cart.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.repository.ProductImgRepository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class)
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductImgRepository productImgRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    OptionGroupRepository optionGroupRepository;

    @Autowired
    OptionValueRepository optionValueRepository;

    @Autowired
    ItemRepository itemRepository;

    @DisplayName("특정 회원의 활성화된 장바구니에서 아이템을 조회한다.")
    @Test
    void findCartAndCartItemByMemberAndActiveCart() {
        // given: 회원 생성
        Member member = Member.builder()
                .loginId("test@ankim.com")
                .pwd("password!")
                .name("또치")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1985, 5, 15))
                .grade(40)
                .gender("F")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        // given: 상품 및 품목 생성
        Product product = ProductFactory.createProduct(
                categoryRepository,
                productRepository,
                optionGroupRepository,
                optionValueRepository,
                productImgRepository,
                itemRepository
        );

        Item item1 = product.getItems().get(0); // 첫 번째 품목
        Item item2 = product.getItems().get(1); // 두 번째 품목

        // given: 장바구니 생성 및 장바구니 아이템 추가
        Cart cart = Cart.create(member, LocalDateTime.now());
        CartItem cartItem1 = CartItem.create(cart, product, item1, 3, LocalDateTime.now());
        CartItem cartItem2 = CartItem.create(cart, product, item2, 5, LocalDateTime.now());

        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);
        cartRepository.save(cart);

        // when: 장바구니 아이템 조회
        Optional<Cart> activeCart = cartRepository.findByMemberAndActiveYn(member, "Y");

        // then: 검증
        assertThat(activeCart).isPresent();
        assertThat(activeCart.get().getCartItems().size()).isEqualTo(2); // 장바구니 아이템 개수 확인
        assertThat(activeCart.get().getActiveYn()).isEqualTo("Y");
        assertThat(activeCart.get().getCartItems().get(0).getItemName()).isEqualTo(item1.getName());
        assertThat(activeCart.get().getCartItems().get(1).getItemName()).isEqualTo(item2.getName());
    }
}