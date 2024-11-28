package shoppingmall.ankim.domain.cart.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.InvalidQuantityException;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.exception.OutOfStockException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberJwtFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
@SpringBootTest
@Transactional
class CartServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private S3Service s3Service;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    @DisplayName("유효하지 않은 accessToken으로 장바구니에 상품을 담으려 할 때 예외 발생")
    void addToCart_InvalidAccessToken_ThrowsJwtValidException() {
        // given
        String loginId = "test@ankim.com";
        String invalidAccessToken = "jwt.token.invalid";

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L))
                .qty(2)
                .build();

        // when & then
        assertThrows(JwtValidException.class, () -> cartService.addToCart(request, invalidAccessToken));
    }

    @Test
    @DisplayName("유효한 accessToken으로 장바구니에 상품을 처음 담는다.")
    void addToCart_ValidAccessToken_Success() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // Mock Product
        Product mockProduct = Mockito.mock(Product.class);
        given(mockProduct.getNo()).willReturn(1L);
        given(mockProduct.getName()).willReturn("테스트 상품");
        given(productRepository.findById(1L)).willReturn(Optional.of(mockProduct));

        // Mock Item
        Item mockItem = Mockito.mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getProduct()).willReturn(mockProduct);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(100); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(10); // 최대 수량

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // Mock 옵션 값 ID
                .qty(2) // 새로 추가할 수량
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(mockItem);

        // when
        cartService.addToCart(request, validAccessToken);

        // then
        Mockito.verify(cartRepository).findByMemberAndActiveYn(member, "Y"); // 활성화된 장바구니 조회 확인
        Mockito.verify(itemRepository).findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList()); // 품목 조회 확인
        Mockito.verify(cartRepository).save(Mockito.any(Cart.class)); // 장바구니 저장 확인
    }

    @Test
    @DisplayName("이미 회원에게 활성화된 Cart가 존재하는 경우 Cart는 추가하지 않고 CartItem만 추가한다.")
    void addToCart_ExistingActiveCart_AddsCartItem() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // Mock Cart
        Cart mockCart = Mockito.mock(Cart.class);
        given(cartRepository.findByMemberAndActiveYn(member, "Y")).willReturn(Optional.of(mockCart));

        // Mock Item
        Item mockItem = Mockito.mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(100); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(10); // 최대 수량

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // Mock 옵션 값 ID
                .qty(2) // 새로 추가할 수량
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(mockItem);

        // when
        cartService.addToCart(request, validAccessToken);

        // then
        Mockito.verify(cartRepository).findByMemberAndActiveYn(member, "Y"); // 활성화된 장바구니 조회 확인
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any(Cart.class)); // 새로운 Cart 저장이 호출되지 않음 확인
        Mockito.verify(mockCart).addCartItem(Mockito.any(CartItem.class)); // CartItem 추가 확인
    }

    @Test
    @DisplayName("동일한 상품을 장바구니에 추가하면 기존 CartItem이 업데이트 된다.")
    void addToCart_SameProduct_UpdatesExistingCartItem() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // Mock Cart
        Cart mockCart = Mockito.mock(Cart.class);

        // Mock CartItem (기존에 장바구니에 추가된 동일 품목)
        CartItem existingCartItem = Mockito.mock(CartItem.class);
        Item mockItem = Mockito.mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(100); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(10); // 최대 수량
        given(existingCartItem.getItem()).willReturn(mockItem);
        given(existingCartItem.getQty()).willReturn(3); // 기존 수량

        // Mock CartItem이 Cart에 포함되어 있는 상태
        given(mockCart.getCartItems()).willReturn(List.of(existingCartItem));
        given(cartRepository.findByMemberAndActiveYn(member, "Y")).willReturn(Optional.of(mockCart));

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // Mock 옵션 값 ID
                .qty(2) // 새로 추가할 수량
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(mockItem);

        // when
        cartService.addToCart(request, validAccessToken);

        // then
        Mockito.verify(existingCartItem).updateQty(Mockito.eq(2)); // regDate를 any로 매칭
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any(Cart.class)); // 새로 저장하지 않음
    }

    @Test
    @DisplayName("존재하지 않는 품목으로 장바구니에 상품을 담으려 할 때 ItemNotFoundException 발생")
    void addToCart_NonexistentItem_ThrowsItemNotFoundException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(4L, 10L))
                .qty(2)
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(null);

        // when & then
        assertThrows(ItemNotFoundException.class, () -> cartService.addToCart(request, validAccessToken));
    }

    @Test
    @DisplayName("재고보다 많은 수량으로 장바구니에 상품을 담으려 할 때 OutOfStockException 발생")
    void addToCart_QuantityExceedsStock_ThrowsOutOfStockException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // Mock Product
        Product mockProduct = Mockito.mock(Product.class);
        given(mockProduct.getNo()).willReturn(1L);
        given(mockProduct.getName()).willReturn("테스트 상품");
        given(productRepository.findById(1L)).willReturn(Optional.of(mockProduct));

        // Mock Item
        Item mockItem = Mockito.mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getProduct()).willReturn(mockProduct);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(50); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(100); // 최대 수량

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // Mock 옵션 값 ID
                .qty(51) // 새로 추가할 수량
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(mockItem);


        // when & then
        assertThrows(OutOfStockException.class, () -> cartService.addToCart(request, validAccessToken));
    }

    @Test
    @DisplayName("최대 수량을 초과하는 경우 InvalidQuantityException 발생")
    void addToCart_QuantityExceedsMaximum_ThrowsInvalidQuantityException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // Mock Product
        Product mockProduct = Mockito.mock(Product.class);
        given(mockProduct.getNo()).willReturn(1L);
        given(mockProduct.getName()).willReturn("테스트 상품");
        given(productRepository.findById(1L)).willReturn(Optional.of(mockProduct));

        // Mock Item
        Item mockItem = Mockito.mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getProduct()).willReturn(mockProduct);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(50); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(10); // 최대 수량

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // Mock 옵션 값 ID
                .qty(11) // 새로 추가할 수량
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(mockItem);

        // when & then
        assertThrows(InvalidQuantityException.class, () -> cartService.addToCart(request, validAccessToken));
    }

    @Test
    @DisplayName("최소 수량보다 적은 경우 InvalidQuantityException 발생")
    void addToCart_QuantityBelowMinimum_ThrowsInvalidQuantityException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        memberRepository.save(member);

        String validAccessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // Mock Product
        Product mockProduct = Mockito.mock(Product.class);
        given(mockProduct.getNo()).willReturn(1L);
        given(mockProduct.getName()).willReturn("테스트 상품");
        given(productRepository.findById(1L)).willReturn(Optional.of(mockProduct));

        // Mock Item
        Item mockItem = Mockito.mock(Item.class);
        given(mockItem.getNo()).willReturn(1L);
        given(mockItem.getProduct()).willReturn(mockProduct);
        given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: L");
        given(mockItem.getQty()).willReturn(50); // 재고
        given(mockItem.getMinQty()).willReturn(1); // 최소 수량
        given(mockItem.getMaxQty()).willReturn(10); // 최대 수량

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L)) // Mock 옵션 값 ID
                .qty(0) // 새로 추가할 수량
                .build();

        given(itemRepository.findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList())).willReturn(mockItem);

        // when & then
        assertThrows(InvalidQuantityException.class, () -> cartService.addToCart(request, validAccessToken));
    }
}