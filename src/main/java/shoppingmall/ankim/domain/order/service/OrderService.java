package shoppingmall.ankim.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartItemNotFoundException;
import shoppingmall.ankim.domain.cart.repository.CartItemRepository;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderCodeGenerationException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.exception.InvalidOrderItemQtyException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    public OrderResponse createTempOrder(String loginId, List<Long> cartItemNoList) {
        LocalDateTime registeredDateTime = LocalDateTime.now();

        // 회원 조회
        Member member = getMember(loginId);

        // 선택한 품목이 있는지 확인
        if(cartItemNoList.isEmpty()) {
            throw new CartItemNotFoundException(NO_SELECTED_CART_ITEM);
        }

        // 장바구니 품목 조회
        List<CartItem> cartItemList = cartItemRepository.findByNoIn(cartItemNoList);
        if (cartItemList.isEmpty() || cartItemList.size() != cartItemNoList.size()) {
            throw new CartItemNotFoundException(CART_ITEM_NOT_FOUND);
        }

        // 장바구니 품목테이블에서 품목(Item) 추출하여 OrderItem 생성
        List<OrderItem> orderItemList = getOrderItems(cartItemList);

        // 임시 주문 생성
        Order tempOrder = Order.tempCreate(orderItemList, member, registeredDateTime);
        Order order = orderRepository.save(tempOrder);

        // 주문 코드 생성 및 저장
        String ordCode = generateOrderCode(order.getOrdNo(), registeredDateTime);
        log.info("ordCode: {}", ordCode);
        order.setOrdCode(ordCode);

        return OrderResponse.tempOf(order);
    }

    private String generateOrderCode(String orderId, LocalDateTime registeredDateTime) {
        try {
            // 현재 날짜 (yyyyMMdd)
            String currentDate = registeredDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String ordCode;
            boolean isDuplicate;
            // 중복되지 않은 주문 코드를 생성할 때까지 반복(do-while)
            do {
                // UUID에서 '-' 제거
                String compactUUID = orderId.replaceAll("-", "");

                // SHA-256 해싱
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(compactUUID.getBytes());

                // 해시값 -> BigInteger 변환
                BigInteger bigInt = new BigInteger(1, hash);

                // BigInteger값을 7자리로 변환하기 위해서 10^targetLength로 나머지 연산
                BigInteger divisor = BigInteger.TEN.pow(7); // 10^7
                BigInteger compressedValue = bigInt.mod(divisor);
                String serialNumber = String.format("%07d", compressedValue); // 7자리 포맷
                // 최종 주문 코드 생성
                ordCode = "ORD" + currentDate + "-" + serialNumber;

                // 중복 확인
                isDuplicate = orderRepository.existsByOrdCode(ordCode);

                if (isDuplicate) {
                    // NOTE 중복 확인 -> 희박한 확률로 중복이 나올 수 있기 때문에 확인해줄 필요가 있다고 판단하였다.
                    log.warn("중복된 주문 코드 발견: {}. 새로운 주문 코드를 생성합니다.", ordCode);
                    // 새로운 UUID 생성 -> 새로운 주문번호 생성을 위함
                    orderId = UUID.randomUUID().toString();
                }
            } while (isDuplicate); // 중복이 아니면 종료
            // 최종적으로 중복되지 않은 주문 코드 반환
            return ordCode;
        } catch (NoSuchAlgorithmException e) {
            throw new OrderCodeGenerationException(ORDER_CODE_GENERATE_FAIL);
        }
    }

    private List<OrderItem> getOrderItems(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(cartItem -> {
                    Item item = cartItem.getItem();
                    Integer qty = cartItem.getQty();
                    // Item 검증
                    if (item == null) {
                        throw new ItemNotFoundException(ITEM_NOT_FOUND); // Item이 null일 경우 예외 발생
                    }
                    // Qty 검증
                    if (qty == null || qty <= 0) {
                        throw new InvalidOrderItemQtyException(ORDER_ITEM_QTY_INVALID); // 수량이 유효하지 않은 경우 예외 발생
                    }
                    return OrderItem.create(item, qty); // 유효한 Item과 Qty로 OrderItem 생성
                })
                .collect(Collectors.toList());
    }


    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }

}
