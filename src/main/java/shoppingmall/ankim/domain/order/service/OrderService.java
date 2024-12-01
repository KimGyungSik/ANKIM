package shoppingmall.ankim.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.dto.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartItemNotFoundException;
import shoppingmall.ankim.domain.cart.exception.CartNotFoundException;
import shoppingmall.ankim.domain.cart.repository.CartItemRepository;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.order.service.request.OrderCreateServiceRequest;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DeliveryService deliveryService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public void createOrder(OrderCreateServiceRequest request,
                                     LocalDateTime registeredDateTime,
                                     String accessToken) {
        // 회원 조회
        String loginId = getLoginId(accessToken);
        Member member = getMember(accessToken);

        // 장바구니에 존재하는 유효한 품목 확인 및 반환
        List<OrderCreateServiceRequest.ItemOrder> validItems = existCartInItemAndQty(request, member);

        // 유효한 품목들을 기반으로 OrderItem 생성
        List<OrderItem> orderItems = getOrderItems(validItems);

        // 품목 재고 조회




        // 주문 생성
//        Order order = orderRepository.save(Order.create(orderItems, member, delivery, registeredDateTime));

        // 주문 코드 생성 및 저장
//        String ordCode = generateOrderCode(order.getOrdNo(), registeredDateTime);
//        order.setOrdCode(ordCode);
//
//        return OrderResponse.of(order);
    }


    private List<OrderCreateServiceRequest.ItemOrder> existCartInItemAndQty(OrderCreateServiceRequest request, Member member) {
        Cart cart = cartRepository.findByMemberAndActiveYn(member, "Y")
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND));

        // 유효한 품목 리스트를 담을 변수
        List<OrderCreateServiceRequest.ItemOrder> validItems = new ArrayList<>();

        // 넘어온 품목들이 실제로 장바구니에 존재하는지 확인
        List<OrderCreateServiceRequest.ItemOrder> items = request.getItems();
        for (OrderCreateServiceRequest.ItemOrder itemOrder : items) {
            Long itemNumber = itemOrder.getItemNumber();
            Integer quantity = itemOrder.getQuantity();

            // 품목 객체 조회 (Item 엔티티 필요)
            Item item = itemRepository.findById(itemNumber)
                    .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND));

            // 장바구니 항목 확인
            Optional<CartItem> cartItem = cartItemRepository.findByCartAndItemAndQty(cart, item, quantity);
            if (cartItem.isEmpty()) {
                throw new CartItemNotFoundException(CART_ITEM_NOT_FOUND);
            }

            // 유효한 품목 추가
            validItems.add(itemOrder);
        }

        return validItems;
    }


    private String generateOrderCode(String orderId, LocalDateTime registeredDateTime) {
        // 현재 날짜 (yyyyMMdd)
        String currentDate = registeredDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 일련번호 추가
        String serialNumber = String.format("%07d", orderId); // orderId를 7자리로 포맷팅

        // 최종 코드 생성
        return "ORD" + currentDate + "-" + serialNumber;
    }


    private List<OrderItem> getOrderItems(List<OrderCreateServiceRequest.ItemOrder> validItems) {
        return validItems.stream()
                .map(itemOrder -> {
                    // 품목 조회
                    Item item = itemRepository.findById(itemOrder.getItemNumber())
                            .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND));
                    // OrderItem 생성
                    return OrderItem.create(item, itemOrder.getQuantity());
                })
                .toList();
    }

    private String getLoginId(String accessToken) {
        // 토큰 유효성 검사(만료 검사도 들어있음)
        if (!jwtTokenProvider.isTokenValidate(accessToken)) {
            throw new JwtValidException(TOKEN_VALIDATION_ERROR);
        }
        // member의 loginId 추출
        return jwtTokenProvider.getUsernameFromToken(accessToken);
    }

    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if(member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }

}
