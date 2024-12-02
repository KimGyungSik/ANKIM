package shoppingmall.ankim.domain.payment.controller.port;

import shoppingmall.ankim.domain.payment.dto.PaymentHistoryResponse;

import java.util.List;

public interface PaymentQueryService {
    // 결제 내역
    List<PaymentHistoryResponse> findPaymentHistories(List<String> orderIds);
}
