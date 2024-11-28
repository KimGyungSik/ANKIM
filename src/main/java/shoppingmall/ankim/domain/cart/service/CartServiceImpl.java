package shoppingmall.ankim.domain.cart.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.item.exception.InvalidQuantityException;
import shoppingmall.ankim.domain.item.exception.OutOfStockException;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Optional;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository; // Member 조회를 위한 Repository
    private final ItemRepository itemRepository; // 품목 조회를 위한 Repository
    private final CartRepository cartRepository; // 장바구니를 위한 Repository

/*
         [장바구니 상품 추가]
         1. accessToken(JWT)에서 memberid 추출
         +. 품목 조회
         +. 품목의 최소/최대 수량 검증
         2. 활성화되어있는 Cart가 있는지 확인
            2.1. 활성화 장바구니 O (activeYn == "Y")
                2.1.1. CartItem에 동일한 상품이 들어있는지 확인
                    - 동일한 상품 존재 O
                      CartItem에 품목번호 등 필수 값 삽입
                    - 동일한 상품 존재 X
                      CartItem에 품목번호 등 필수 값 삽입
            2.2. 활성화 장바구니 X (Cart가 null이거나 Cart의 activeYn == "N")
                2.2.1. Cart 생성 후 CartItem에 품목번호 등 필수 값 삽입
 */
    @Override
    public void addToCart(AddToCartServiceRequest request, String accessToken) {
        LocalDateTime now = LocalDateTime.now();
        // 토큰 유효성 검사(만료 검사도 들어있음)
        if (!jwtTokenProvider.isTokenValidate(accessToken)) {
            throw new JwtValidException(TOKEN_VALIDATION_ERROR);
        }
        // member의 loginId 추출
        String loginId = jwtTokenProvider.getUsernameFromToken(accessToken);
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }

        // 품목 조회
        Item item = Optional.ofNullable(itemRepository.findItemByOptionValuesAndProduct(
                request.getProductNo(),
                request.getOptionValueNoList()
        )).orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND));
/*
        장바구니에 담으려는 수량이
        재고량보다 작은지 확인
        품목의 min/max 수량 안에 포함되는지 확인
*/
        Integer qty = request.getQty();
        if (qty > item.getQty()) { // 재고보다 많이 주문하는 경우
            throw new OutOfStockException(OUT_OF_STOCK);
        }

        if (qty > item.getMaxQty()) { // 최대 주문 수량을 초과하는 경우
            throw new InvalidQuantityException(QUANTITY_EXCEED_MAXIMUM);
        }

        if (qty < item.getMinQty()) { // 최소 주문 수량보다 적은 경우
            throw new InvalidQuantityException(QUANTITY_BELOW_MINIMUM);
        }

        // mem_no를 통해서 cart 엔티티에 장바구니가 존재하는지 확인
        // 장바구니가 없으면 새로운 장바구니 생성
        Optional<Cart> activeCart = cartRepository.findByMemberAndActiveYn(member, "Y");
        Cart cart;

        if (activeCart.isPresent()) { // 활성화된 장바구니 존재 O
            // 기존 장바구니 사용
            cart = activeCart.get();

            // 동일 품목 존재 여부 확인
            Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getItem().equals(item))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                // 동일 품목 존재 시 수량 업데이트
                CartItem cartItem = existingCartItem.get();
                cartItem.updateQty(qty);
            } else {
                // 동일 품목 미존재 시 새 품목 추가
                CartItem newCartItem = CartItem.create(cart, item, qty, now);
                cart.addCartItem(newCartItem);
            }
        } else { // 활성화된 장바구니 존재 X
            // 새로운 장바구니 생성 후 품목 추가
            cart = Cart.create(member, now);
            CartItem newCartItem = CartItem.create(cart, item, qty, now);
            cart.addCartItem(newCartItem);
            cartRepository.save(cart); // 새 장바구니 저장
        }
    }

}
