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
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartItemLimitExceededException;
import shoppingmall.ankim.domain.cart.repository.CartItemRepository;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.InvalidQuantityException;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.exception.NoOutOfStockException;
import shoppingmall.ankim.domain.item.exception.OutOfStockException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.factory.ProductFactory;

import java.time.LocalDateTime;
import java.util.*;

        import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
        import static org.mockito.internal.verification.VerificationModeFactory.times;
import static shoppingmall.ankim.global.exception.ErrorCode.CART_ITEM_LIMIT_EXCEEDED;
import static shoppingmall.ankim.global.exception.ErrorCode.NO_OUT_OF_STOCK_ITEMS;

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
    private CartItemRepository cartItemRepository;

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
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        verify(cartRepository).findByMemberAndActiveYn(member, "Y"); // 활성화된 장바구니 조회 확인
        verify(itemRepository).findItemByOptionValuesAndProduct(request.getProductNo(), request.getOptionValueNoList()); // 품목 조회 확인
        verify(cartRepository).save(any(Cart.class)); // 장바구니 저장 확인
    }

    @Test
    @DisplayName("이미 회원에게 활성화된 Cart가 존재하는 경우 Cart는 추가하지 않고 CartItem만 추가한다.")
    void addToCart_ExistingActiveCart_AddsCartItem() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        verify(cartRepository).findByMemberAndActiveYn(member, "Y"); // 활성화된 장바구니 조회 확인
        verify(cartRepository, never()).save(any(Cart.class)); // 새로운 Cart 저장이 호출되지 않음 확인
        verify(mockCart).addCartItem(any(CartItem.class)); // CartItem 추가 확인
    }

    @Test
    @DisplayName("동일한 상품을 장바구니에 추가하면 기존 CartItem이 업데이트 된다.")
    void addToCart_SameProduct_UpdatesExistingCartItem() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        verify(existingCartItem).updateQuantityWithDate(Mockito.eq(2)); // regDate를 any로 매칭
        verify(cartRepository, never()).save(any(Cart.class)); // 새로 저장하지 않음
    }

    @Test
    @DisplayName("존재하지 않는 품목으로 장바구니에 상품을 담으려 할 때 ItemNotFoundException 발생")
    void addToCart_NonexistentItem_ThrowsItemNotFoundException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        String validAccessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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

    @Test
    @DisplayName("장바구니 페이지 진입 시 활성화된 장바구니가 없으면 비어있는 새 장바구니를 생성한다.")
    void shouldCreateNewCartIfNoActiveCart() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        given(cartRepository.findByMemberAndActiveYn(member, "Y")).willReturn(Optional.empty());

        // when
        List<CartItemsResponse> cartItems = cartService.getCartItems(accessToken);

        // then
        assertThat(cartItems).isEmpty(); // 빈 장바구니 반환 확인
        verify(cartRepository).save(any(Cart.class)); // 새로운 장바구니 저장 호출 확인
    }

    @Test
    @DisplayName("장바구니 페이지 진입 시 활성화된 장바구니와 한 개의 품목이 담겨 있을 경우 CartItemsResponse를 반환한다.")
    void shouldReturnCartItemsResponseWhenActiveCartExists() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        Cart cart = Cart.create(member, LocalDateTime.now());
        em.persist(cart);

        Product product = ProductFactory.createProduct(em); // 상품 생성
        Item item = product.getItems().get(0); // 첫 번째 품목
        CartItem cartItem = CartItem.create(cart, product, item, 2, LocalDateTime.now()); // 장바구니 품목 생성
        cart.addCartItem(cartItem);
        em.persist(cartItem);

        given(cartRepository.findByMemberAndActiveYn(member, "Y")).willReturn(Optional.of(cart));

        // when
        List<CartItemsResponse> cartItemsResponses = cartService.getCartItems(accessToken);

        // then
        assertThat(cartItemsResponses).hasSize(1); // 품목 개수 확인

        CartItemsResponse response = cartItemsResponses.get(0);
        assertThat(response.getCartNo()).isEqualTo(cart.getNo());
        assertThat(response.getCartItemNo()).isEqualTo(cartItem.getNo());
        assertThat(response.getProductName()).isEqualTo(product.getName());
        assertThat(response.getItemName()).isEqualTo(cartItem.getItemName());
        assertThat(response.getQty()).isEqualTo(cartItem.getQty());
        assertThat(response.getItemQty()).isEqualTo(item.getQty());
        assertThat(response.getSellingStatus()).isEqualTo(item.getSellingStatus());
        assertThat(response.getOrigPrice()).isEqualTo(product.getOrigPrice());
        assertThat(response.getDiscRate()).isEqualTo(product.getDiscRate());
        assertThat(response.getSellPrice()).isEqualTo(product.getSellPrice());
        assertThat(response.getAddPrice()).isEqualTo(item.getAddPrice());
        assertThat(response.getTotalPrice()).isEqualTo(item.getTotalPrice());
        assertThat(response.getFreeShip()).isEqualTo(product.getFreeShip());
        assertThat(response.getShipFee()).isEqualTo(product.getShipFee());
        assertThat(response.getMaxQty()).isEqualTo(item.getMaxQty());
        assertThat(response.getMinQty()).isEqualTo(item.getMinQty());
    }

    @Test
    @DisplayName("장바구니 페이지 진입 시 활성화된 장바구니에 10개의 품목이 있을 경우 모든 CartItemsResponse를 반환한다.")
    void shouldReturnAllCartItemsResponseWhenActiveCartHasMoreThanTenItems() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Cart
        Cart mockCart = Mockito.mock(Cart.class);
        given(mockCart.getNo()).willReturn(1L);
        given(mockCart.getMember()).willReturn(member);

        // Mock Product와 Item 생성
        List<CartItem> mockCartItems = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            Product mockProduct = Mockito.mock(Product.class);
            given(mockProduct.getNo()).willReturn((long) i);
            given(mockProduct.getName()).willReturn("테스트 상품 " + i);
            given(mockProduct.getOrigPrice()).willReturn(20000 + (i * 1000));
            given(mockProduct.getDiscRate()).willReturn(10);
            given(mockProduct.getSellPrice()).willReturn(18000 + (i * 900));
            given(mockProduct.getFreeShip()).willReturn(i % 2 == 0 ? "Y" : "N");
            given(mockProduct.getShipFee()).willReturn(i % 2 == 0 ? 0 : 3000);

            for (int j = 1; j <= random.nextInt(2) + 1; j++) {
                Item mockItem = Mockito.mock(Item.class);
                given(mockItem.getNo()).willReturn((long) (i * 10 + j));
                given(mockItem.getProduct()).willReturn(mockProduct);
                given(mockItem.getName()).willReturn("색상: 블랙, 사이즈: " + (j == 1 ? "M" : "L"));
                given(mockItem.getQty()).willReturn(100);
                given(mockItem.getMinQty()).willReturn(1);
                given(mockItem.getMaxQty()).willReturn(10);
                given(mockItem.getAddPrice()).willReturn(1000 * j);
                given(mockItem.getTotalPrice()).willReturn(19000 + (i * 900) + (j * 1000));
                given(mockItem.getSellingStatus()).willReturn(ProductSellingStatus.SELLING);

                CartItem mockCartItem = Mockito.mock(CartItem.class);
                given(mockCartItem.getNo()).willReturn((long) (i * 100 + j));
                given(mockCartItem.getItem()).willReturn(mockItem);
                given(mockCartItem.getQty()).willReturn(random.nextInt(5) + 1);
//                given(mockCartItem.getItemName()).willReturn(mockItem.getName()); //  Mocking 충돌 때문에 주석
//                given(mockCartItem.getThumbNailImgUrl()).willReturn("http://example.com/item-image-" + j + ".jpg");
                given(mockCartItem.getProduct()).willReturn(mockProduct);

                mockCartItems.add(mockCartItem);
            }
        }

        given(mockCart.getCartItems()).willReturn(mockCartItems);
        given(cartRepository.findByMemberAndActiveYn(member, "Y")).willReturn(Optional.of(mockCart));

        // when
        List<CartItemsResponse> cartItemsResponses = cartService.getCartItems(accessToken);

        // then
        assertThat(cartItemsResponses).hasSize(mockCartItems.size());

        for (int i = 0; i < mockCartItems.size(); i++) {
            CartItemsResponse response = cartItemsResponses.get(i);
            CartItem cartItem = mockCartItems.get(i);
            Product product = cartItem.getProduct();
            Item item = cartItem.getItem();

            assertThat(response.getCartNo()).isEqualTo(mockCart.getNo());
            assertThat(response.getCartItemNo()).isEqualTo(cartItem.getNo());
            assertThat(response.getProductName()).isEqualTo(product.getName());
            assertThat(response.getItemName()).isEqualTo(cartItem.getItemName());
            assertThat(response.getQty()).isEqualTo(cartItem.getQty());
            assertThat(response.getItemQty()).isEqualTo(item.getQty());
            assertThat(response.getSellingStatus()).isEqualTo(item.getSellingStatus());
            assertThat(response.getOrigPrice()).isEqualTo(product.getOrigPrice());
            assertThat(response.getDiscRate()).isEqualTo(product.getDiscRate());
            assertThat(response.getSellPrice()).isEqualTo(product.getSellPrice());
            assertThat(response.getAddPrice()).isEqualTo(item.getAddPrice());
            assertThat(response.getTotalPrice()).isEqualTo(item.getTotalPrice());
            assertThat(response.getFreeShip()).isEqualTo(product.getFreeShip());
            assertThat(response.getShipFee()).isEqualTo(product.getShipFee());
            assertThat(response.getMaxQty()).isEqualTo(item.getMaxQty());
            assertThat(response.getMinQty()).isEqualTo(item.getMinQty());
        }
    }

    @Test
    @DisplayName("장바구니에 담긴 품목의 수량 변경을 성공한다.")
    void updateCartItemQuantity_SUCCESS() { // FIXME 테스트 작성 필요(when & then)
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

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
        given(mockCart.getMember()).willReturn(member);

        given(cartRepository.findById(1L)).willReturn(Optional.of(mockCart));

        // Mock CartItem
        CartItem mockCartItem = mock(CartItem.class);
        given(mockCartItem.getProduct()).willReturn(mockProduct);
        given(mockCartItem.getNo()).willReturn(1L);
        given(mockCartItem.getItem()).willReturn(mockItem);
        given(mockCartItem.getItemName()).willReturn("테스트 품목");
        given(mockCartItem.getCart()).willReturn(mockCart);
        given(mockCartItem.getQty()).willReturn(3);

        given(cartItemRepository.findByNoAndCart_Member(1L, member)).willReturn(Optional.of(mockCartItem));

        // when
        Integer newQty = 1;
        cartService.updateCartItemQuantity(accessToken, mockCartItem.getNo(), newQty);

        // then
        verify(cartItemRepository, times(1)).findByNoAndCart_Member(mockCartItem.getNo(), member);
        verify(mockCartItem, times(1)).changeQuantity(newQty); // 수량 변경 메서드 호출 확인
    }

    @Test
    @DisplayName("장바구니에 담긴 품목의 수량 변경하지만 재고보다 많이 요청하여 변경을 실패한다.")
    void updateCartItemQuantity_FailDueToOutOfStock() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Item
        Item mockItem = mock(Item.class);
        given(mockItem.getQty()).willReturn(50); // 재고를 50으로 설정

        // Mock CartItem
        CartItem mockCartItem = mock(CartItem.class);
        given(mockCartItem.getItem()).willReturn(mockItem);

        // Mock CartItemRepository
        given(cartItemRepository.findByNoAndCart_Member(1L, member)).willReturn(Optional.of(mockCartItem));

        // 요청 수량이 재고보다 많음
        Integer requestedQty = 60;

        // when & then
        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            cartService.updateCartItemQuantity(accessToken, 1L, requestedQty);
        });

        // 검증
        assertThat(exception.getMessage()).isEqualTo("재고가 부족합니다.");
        verify(cartItemRepository, times(1)).findByNoAndCart_Member(1L, member);
        verify(mockCartItem, never()).changeQuantity(anyInt()); // 수량 변경 메서드가 호출되지 않아야 함
    }

    @Test
    @DisplayName("장바구니에 담긴 품목의 수량 변경하지만 최소 주문 수량보다 적게 요청하여 변경을 실패한다.")
    void updateCartItemQuantity_FailDueToQuantityBelowMinimum() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Item
        Item mockItem = mock(Item.class);
        given(mockItem.getMinQty()).willReturn(1); // 최소 주문 수량 설정

        // Mock CartItem
        CartItem mockCartItem = mock(CartItem.class);
        given(mockCartItem.getItem()).willReturn(mockItem);

        // Mock CartItemRepository
        given(cartItemRepository.findByNoAndCart_Member(1L, member)).willReturn(Optional.of(mockCartItem));

        // 요청 수량이 최소 주문 수량보다 적음
        Integer requestedQty = 0;

        // when & then
        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class, () -> {
            cartService.updateCartItemQuantity(accessToken, 1L, requestedQty);
        });

        // 검증
        assertThat(exception.getMessage()).isEqualTo("최소 주문 수량 보다 적습니다.");
        verify(cartItemRepository, times(1)).findByNoAndCart_Member(1L, member);
        verify(mockCartItem, never()).changeQuantity(anyInt()); // 수량 변경 메서드가 호출되지 않아야 함
    }

    @Test
    @DisplayName("장바구니에 담긴 품목의 수량 변경하지만 최대 주문 수량을 초과하여 변경을 실패한다.")
    void updateCartItemQuantity_FailDueToQuantityExceedMaximum() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Item
        Item mockItem = mock(Item.class);
        given(mockItem.getQty()).willReturn(50); // 재고를 50으로 설정
        given(mockItem.getMaxQty()).willReturn(10); // 최대 주문 수량 설정

        // Mock CartItem
        CartItem mockCartItem = mock(CartItem.class);
        given(mockCartItem.getItem()).willReturn(mockItem);

        // Mock CartItemRepository
        given(cartItemRepository.findByNoAndCart_Member(1L, member)).willReturn(Optional.of(mockCartItem));

        // 요청 수량이 최대 주문 수량을 초과
        Integer requestedQty = 15;

        // when & then
        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class, () -> {
            cartService.updateCartItemQuantity(accessToken, 1L, requestedQty);
        });

        assertThat(exception.getMessage()).isEqualTo("최대 주문 수량을 초과했습니다.");
        verify(cartItemRepository, times(1)).findByNoAndCart_Member(1L, member);
        verify(mockCartItem, never()).changeQuantity(anyInt()); // 수량 변경 메서드가 호출되지 않아야 함
    }

    @Test
    @DisplayName("재고가 0인 장바구니 품목을 비활성화한다.")
    void deactivateOutOfStockItems() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Items
        Item mockItem1 = mock(Item.class);
        given(mockItem1.getQty()).willReturn(0); // 재고 0으로 설정

        Item mockItem2 = mock(Item.class);
        given(mockItem2.getQty()).willReturn(0); // 재고 0으로 설정

        // Mock CartItems
        CartItem mockCartItem1 = mock(CartItem.class);
        given(mockCartItem1.getItem()).willReturn(mockItem1);

        CartItem mockCartItem2 = mock(CartItem.class);
        given(mockCartItem2.getItem()).willReturn(mockItem2);

        given(cartItemRepository.findOutOfStockItems(member))
                .willReturn(List.of(mockCartItem1, mockCartItem2));

        // when
        cartService.deactivateOutOfStockItems(accessToken);

        // then
        verify(mockCartItem1, times(1)).deactivate();
        verify(mockCartItem2, times(1)).deactivate();
    }

    @Test
    @DisplayName("재고가 0인 품목만 비활성화하고, 재고가 0이 아닌 품목은 상태를 변경하지 않는다.")
    void deactivateOutOfStockItems_ShouldOnlyDeactivateItemsWithZeroStock() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Items
        Item outOfStockItem1 = mock(Item.class);
        given(outOfStockItem1.getQty()).willReturn(0); // 재고 0으로 설정

        Item outOfStockItem2 = mock(Item.class);
        given(outOfStockItem2.getQty()).willReturn(0); // 재고 0으로 설정

        Item inStockItem = mock(Item.class);
        given(inStockItem.getQty()).willReturn(10); // 재고 10으로 설정

        // Mock CartItems
        CartItem outOfStockCartItem1 = mock(CartItem.class);
        given(outOfStockCartItem1.getItem()).willReturn(outOfStockItem1);

        CartItem outOfStockCartItem2 = mock(CartItem.class);
        given(outOfStockCartItem2.getItem()).willReturn(outOfStockItem2);

        CartItem inStockCartItem = mock(CartItem.class);
        given(inStockCartItem.getItem()).willReturn(inStockItem);

        // Mock deactivate 동작 설정
        doNothing().when(outOfStockCartItem1).deactivate();
        doNothing().when(outOfStockCartItem2).deactivate();
        doNothing().when(inStockCartItem).deactivate();

        // Mock Repository
        // CartItem의 상태가 변경이 안되는 것을 확인하기 위해서 inStockCartItem은 재고가 있지만 조회가 되었다고 가정하고 진행한다.
        given(cartItemRepository.findOutOfStockItems(member))
                .willReturn(List.of(outOfStockCartItem1, outOfStockCartItem2, inStockCartItem));

        // when
        cartService.deactivateOutOfStockItems(accessToken);

        // then
        verify(outOfStockCartItem1, times(1)).deactivate(); // 재고 0인 품목 상태 변경
        verify(outOfStockCartItem2, times(1)).deactivate(); // 재고 0인 품목 상태 변경
        verify(inStockCartItem, never()).deactivate(); // 재고 0이 아닌 품목 상태 변경 X
    }

    @Test
    @DisplayName("품절된 상품이 없을 때 NoOutOfStockException이 발생한다.")
    void deactivateOutOfStockItems_NoOutOfStockItems_ThrowsNoOutOfStockException() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock 품절 상품이 없는 상황
        given(cartItemRepository.findOutOfStockItems(member)).willReturn(List.of());

        // when & then
        NoOutOfStockException exception = assertThrows(NoOutOfStockException.class, () -> {
            cartService.deactivateOutOfStockItems(accessToken);
        });

        // 검증
        assertThat(exception.getMessage()).isEqualTo(NO_OUT_OF_STOCK_ITEMS.getMessage());
        verify(cartItemRepository, times(1)).findOutOfStockItems(member);
    }

    @Test
    @DisplayName("10개의 장바구니 품목 중 3개를 선택하여 삭제에 성공한다.")
    void deactivateSelectedItems_RandomSelection_Success() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // Mock Cart 생성
        Cart mockCart = mock(Cart.class);
        given(mockCart.getMember()).willReturn(member);

        // Mock 10개의 장바구니 품목 생성 + 품목 ID 리스트 생성 및 섞기
        List<CartItem> allCartItems = new ArrayList<>();
        List<Long> allIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            CartItem cartItem = mock(CartItem.class);
            given(cartItem.getCart()).willReturn(mockCart); // Mock Cart 연결
            given(cartItem.getCart().getMember()).willReturn(member);
            given(cartItem.getNo()).willReturn(i); // 품목 ID 설정

            allCartItems.add(cartItem);
            allIds.add(i);
        }
        Collections.shuffle(allIds); // ID 리스트 무작위 섞기

        // 첫 3개 ID 선택
        List<Long> selectedIds = allIds.subList(0, 3);

        // Repository에서 선택된 품목만 반환되도록 설정
        List<CartItem> selectedCartItems = allCartItems.stream()
                .filter(cartItem -> selectedIds.contains(cartItem.getNo()))
                .toList();
        given(cartItemRepository.findAllById(selectedIds)).willReturn(selectedCartItems);

        // when
        cartService.deactivateSelectedItems(accessToken, selectedIds);

        // then
        // 선택된 품목만 상태 변경(deactivate) 확인
        for (CartItem cartItem : allCartItems) {
            if (selectedIds.contains(cartItem.getNo())) {
                verify(cartItem, times(1)).deactivate();
            } else {
                verify(cartItem, never()).deactivate();
            }
        }
    }

    @Test
    @DisplayName("장바구니 품목이 최대 개수를 초과하면 CART_ITEM_LIMIT_EXCEEDED 예외가 발생한다.")
    void addToCart_ShouldThrowException_WhenCartItemLimitExceeded() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // 장바구니의 최대 품목 개수
        int maxCartItems = 100;

        // Mock AddToCartServiceRequest 생성
        AddToCartServiceRequest request = mock(AddToCartServiceRequest.class);
        given(request.getProductNo()).willReturn(1L);
        given(request.getOptionValueNoList()).willReturn(List.of(1L, 2L));
        given(request.getQty()).willReturn(2);

        // Mock 장바구니 품목 개수를 초과하도록 설정
        when(cartItemRepository.countActiveCartItems(member)).thenReturn(maxCartItems + 1);

        // when & then
        assertThatThrownBy(() -> cartService.addToCart(request, accessToken))
                .isInstanceOf(CartItemLimitExceededException.class)
                .hasMessageContaining(CART_ITEM_LIMIT_EXCEEDED.getMessage());
    }
}