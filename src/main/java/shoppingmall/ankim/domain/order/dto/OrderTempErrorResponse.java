package shoppingmall.ankim.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shoppingmall.ankim.domain.address.dto.ExistAddressResponse;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.dto.OrderItemResponse;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTempErrorResponse {
    private String message;    // 에러코드
    private String referer;    // 이전 페이지 주소

    public static OrderTempErrorResponse of(String message, String referer) {
        return OrderTempErrorResponse.builder()
                .message(message)
                .referer(referer)
                .build();
    }
}
