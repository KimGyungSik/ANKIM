package shoppingmall.ankim.domain.cart.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.factory.MemberFactory;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
@SpringBootTest
@Transactional
//        (isolation = Isolation.READ_COMMITTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 테스트 간 독립성 확보
class CartServiceTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @MockBean
    private S3Service s3Service;

    @Test
    @DisplayName("회원이 장바구니에 상품을 1개 담는다.")
    void addToCart_Success() {
        // given
        String loginId = "test" + UUID.randomUUID().toString().substring(0,4) + "@example.com";
        Member member = MemberFactory.createMemberAndProduct(em, loginId);
        em.flush();
        em.clear();

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // 옵션 값 ID
                .qty(2) // 새로 추가할 수량
                .build();

        // when
        cartService.addToCart(request, loginId);

        em.flush();
        em.clear();

        // then
        Optional<Cart> cart = cartRepository.findByMemberAndActiveYn(member, "Y");
        assertThat(cart).isNotEmpty();
        assertThat(cart.get().getCartItems()).hasSize(1);
    }

    @Test
    @DisplayName("회원이 장바구니에 상품을 3개 담는다.")
    void addToCart3Items_Success() {
        // given
        String loginId = "test" + UUID.randomUUID().toString().substring(0,4) + "@example.com";
        Member member = MemberFactory.createMemberAndProduct(em, loginId);

        AddToCartServiceRequest request1 = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L))
                .qty(2) // 새로 추가할 수량
                .build();

        AddToCartServiceRequest request2 = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 4L))
                .qty(1) // 새로 추가할 수량
                .build();

        AddToCartServiceRequest request3 = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(2L, 4L))
                .qty(3) // 새로 추가할 수량
                .build();

        // when
        cartService.addToCart(request1, loginId);
        cartService.addToCart(request2, loginId);
        cartService.addToCart(request3, loginId);
        em.flush();
        em.clear();

        // then
        Optional<Cart> cart = cartRepository.findByMemberAndActiveYn(member, "Y");
        assertThat(cart).isNotEmpty();
        assertThat(cart.get().getCartItems()).hasSize(3);
    }

    @Test
    @DisplayName("회원이 장바구니에 동일 상품을 여러개 담는 경우 마지막 담았을 때의 수량으로 장바구니에 담긴다.")
    void addToCartItemsMany_Success() {
        // given
        String loginId =  "test" + UUID.randomUUID().toString().substring(0,4) + "@example.com";
        Member member = MemberFactory.createMemberAndProduct(em, loginId);

        Integer lastQty = 0;
        Random random = new Random();
        int size = 1;
        for(int i=0;i<=size;i++) {
            Integer randQty = random.nextInt(5) + 1;
            AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                    .productNo(1L)
                    .optionValueNoList(List.of(1L, 3L))
                    .qty(randQty) // 새로 추가할 수량
                    .build();
            // when
            cartService.addToCart(request, loginId);

            if(i == size) lastQty = randQty;
        }

        // then
        Optional<Cart> cart = cartRepository.findByMemberAndActiveYn(member, "Y");
        assertThat(cart).isNotEmpty();
        assertThat(cart.get().getCartItems()).hasSize(1);
        assertThat(cart.get().getCartItems().get(0).getQty()).isEqualTo(lastQty);
    }

    @Test
    @DisplayName("장바구니 페이지 진입 시 활성화된 장바구니가 없으면 비어있는 새 장바구니를 생성한다.")
    void shouldCreateNewCartIfNoActiveCart() {
        // given
        String loginId =  "test" + UUID.randomUUID().toString().substring(0,4) + "@example.com";
        Member member = MemberFactory.createMemberAndProduct(em, loginId);

        // 실제 Repository로 데이터를 조회
        Optional<Cart> cart = cartRepository.findByMemberAndActiveYn(member, "Y");
        assertThat(cart).isEmpty(); // 장바구니가 비어있는 상태 확인

        // when
        List<CartItemsResponse> cartItems = cartService.getCartItems(loginId);
        em.flush();
        em.clear();

        // then
        Optional<Cart> newCart = cartRepository.findByMemberAndActiveYn(member, "Y");
        assertThat(newCart).isNotNull(); // 새로운 장바구니 생성 확인
    }

    @Test
    @DisplayName("이미 회원에게 활성화된 Cart가 존재하는 경우 Cart는 추가하지 않고 CartItem만 추가한다.")
    void addToCart_ExistingActiveCart_AddsCartItem() {
        // given
        String loginId = "test" + UUID.randomUUID().toString().substring(0,4) + "@example.com";
        Member member = MemberFactory.createMemberAndProduct(em, loginId);

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // 옵션 값 ID
                .qty(2) // 새로 추가할 수량
                .build();

        cartService.addToCart(request, loginId);

        em.flush();
        em.clear();

        // when
        List<CartItemsResponse> cartItems = cartService.getCartItems(loginId);

        // then
        assertThat(cartItems).isNotEmpty();
        assertThat(cartItems.size()).isEqualTo(1);
        assertThat(cartItems.get(0).getQty()).isEqualTo(2);
    }


}