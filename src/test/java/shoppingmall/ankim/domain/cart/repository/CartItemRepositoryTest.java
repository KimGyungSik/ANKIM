package shoppingmall.ankim.domain.cart.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CartItemRepositoryTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemRepositoryTest cartItemRepositoryTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원의 장바구니에서 특정 품목을 Mock으로 조회한다.")
    void findByIdAndCart_Member_Success_WithMock() {
        // Mock Member
        Member mockMember = mock(Member.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(mockMember));

        // Mock Product
        Product mockProduct = mock(Product.class);
        given(mockProduct.getNo()).willReturn(1L);
        given(mockProduct.getName()).willReturn("테스트 상품");
        given(productRepository.findById(1L)).willReturn(Optional.of(mockProduct));

        // Mock Item
        Item mockItem = mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getProduct()).willReturn(mockProduct);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(50); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(10); // 최대 수량

        // Mock Cart
        Cart mockCart = mock(Cart.class);
        given(mockCart.getMember()).willReturn(mockMember);

        given(cartRepository.findById(1L)).willReturn(Optional.of(mockCart));

        // Mock CartItem
        CartItem mockCartItem = mock(CartItem.class);
        given(mockCartItem.getProduct()).willReturn(mockProduct);
        given(mockCartItem.getNo()).willReturn(1L);
        given(mockCartItem.getItem()).willReturn(mockItem);
        given(mockCartItem.getItemName()).willReturn("테스트 품목");
        given(mockCartItem.getCart()).willReturn(mockCart);
        given(mockCartItem.getQty()).willReturn(3);

        given(cartItemRepository.findByNoAndCart_Member(1L, mockMember)).willReturn(Optional.of(mockCartItem));

        // when
        CartItem foundCartItem = cartItemRepository.findByNoAndCart_Member(1L, mockMember)
                .orElse(null);

        // then
        assertThat(foundCartItem).isNotNull();
        assertThat(foundCartItem.getQty()).isEqualTo(mockCartItem.getQty());
        assertThat(foundCartItem.getItem().getName()).isEqualTo(mockCartItem.getItem().getName());
        assertThat(foundCartItem.getItem().getProduct().getName()).isEqualTo(mockCartItem.getItem().getProduct().getName());
    }
}