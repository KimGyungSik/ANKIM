package shoppingmall.ankim.domain.payment.controller.port;

import shoppingmall.ankim.domain.address.dto.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;

import java.util.List;

public interface PaymentService {
    // 클라이언트 결제 요청처리 & 재고 감소
    PaymentResponse requestTossPayment(PaymentCreateServiceRequest request,
                                       DeliveryCreateServiceRequest deliveryRequest,
                                       MemberAddressCreateServiceRequest addressRequest);

    // 결제 성공 시 처리 & 배송지 저장 & 주문 상태 (결제완료) & 장바구니 주문 상품 비활성화 (장바구니 비우기)
    PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Integer amount);

    // 결제 실패 시 처리 & 재고 복구 & 주문 상태 (결제실패)
    PaymentFailResponse tossPaymentFail(String code, String message, String orderId);

    // 결제 취소 시 처리 & 재고 복구 & 주문 상태 (결제취소)
    PaymentCancelResponse cancelPayment(String paymentKey, String cancelReason);
}
