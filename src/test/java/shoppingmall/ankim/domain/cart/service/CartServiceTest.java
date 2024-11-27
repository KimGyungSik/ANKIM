package shoppingmall.ankim.domain.cart.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.cart.service.CartServiceImpl;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
//        ,"jwt.secret=23aca2ed0815953971212551d4abbfe1ce73b58230ef7be989f3a32f79a0d360"
//        ,"jwt.access.token.expire.time=3600000"
//        ,"jwt.refresh.token.expire.time=600000"
//        ,"jwt.refresh.token.remember.time=604800000"
})
@SpringBootTest
@Transactional
class CartServiceTest {

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
                .optionValueNoList(List.of(1L, 2L))
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
        memberRepository.save(member);

        CustomUserDetails userDetails = new CustomUserDetails(member);

        String validAccessToken = jwtTokenProvider.generateAccessToken(userDetails, "access"); // NOTE access 토큰 생성

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
        given(itemRepository.findById(1L)).willReturn(Optional.of(mockItem));

        // 장바구니에 담기 선택
        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 2L)) // Mock 옵션 값 ID
                .qty(2) // Mock 수량
                .build();

        // when
        cartService.addToCart(request, validAccessToken);

        // then
        Mockito.verify(cartRepository).findByMemberAndActiveYn(member, "Y"); // 활성화된 장바구니 조회 확인
        Mockito.verify(itemRepository).findById(1L); // 품목 조회 확인
        Mockito.verify(cartRepository).save(Mockito.any(Cart.class)); // 장바구니 저장 확인
    }

    @Test
    @DisplayName("이미 회원에게 활성화된 Cart가 존재하는 경우 Cart에 CartItem 추가")
    void addToCart_ExistingActiveCart_AddsCartItem() {
        // given
        String loginId = "test@ankim.com";

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
        memberRepository.save(member);

        CustomUserDetails userDetails = new CustomUserDetails(member);
        String validAccessToken = jwtTokenProvider.generateAccessToken(userDetails, "access");

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
        given(itemRepository.findById(1L)).willReturn(Optional.of(mockItem));

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 2L)) // Mock 옵션 값 ID
                .qty(3) // Mock 수량
                .build();

        // when
        cartService.addToCart(request, validAccessToken);

        // then
        Mockito.verify(cartRepository).findByMemberAndActiveYn(member, "Y"); // 활성화된 장바구니 조회 확인
        Mockito.verify(mockCart).addCartItem(Mockito.any(CartItem.class)); // CartItem 추가 확인
    }

    @Test
    @DisplayName("동일한 상품을 장바구니에 추가하면 기존 CartItem이 업데이트 된다")
    void addToCart_SameProduct_UpdatesExistingCartItem() {
        // given
        String loginId = "test@ankim.com";

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
        memberRepository.save(member);

        CustomUserDetails userDetails = new CustomUserDetails(member);
        String validAccessToken = jwtTokenProvider.generateAccessToken(userDetails, "access");

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
        given(itemRepository.findById(1L)).willReturn(Optional.of(mockItem));

        AddToCartServiceRequest request = AddToCartServiceRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 2L)) // Mock 옵션 값 ID
                .qty(2) // 새로 추가할 수량
                .build();

        // when
        cartService.addToCart(request, validAccessToken);

        // then
        Mockito.verify(existingCartItem).updateQty(Mockito.eq(2), Mockito.any(LocalDateTime.class)); // regDate를 any로 매칭
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any(Cart.class)); // 새로 저장하지 않음
    }
}