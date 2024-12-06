package shoppingmall.ankim.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/temp-order")
public class OrderTempController {

    private final OrderService orderService;

    /*
     * 임시 주문 생성 API
     * 1. 장바구니에서 선택한 상품의 cartItemNo를 받아온다.
     * 2. 전달받은 cartItemNo를 이용하여 cartItem을 조회 -> itemNo(품목번호)를 받아온다.
     **/
    @PostMapping
    public ApiResponse<OrderResponse> createTempOrder(
            @RequestBody List<Long> cartItemNoList
    ) {

        String loginId = getLoginId();

        OrderResponse tempOrder = orderService.createTempOrder(loginId, cartItemNoList);
        return ApiResponse.ok(tempOrder);
    }

    private static String getLoginId() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 로그인 ID
    }

}
