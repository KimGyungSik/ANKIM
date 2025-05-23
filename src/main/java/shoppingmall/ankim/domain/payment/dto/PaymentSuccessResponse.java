package shoppingmall.ankim.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessResponse {
    String mid; // 가맹점 Id -> tosspayments
    String version; // Payment 객체 응답 버전
    String paymentKey;
    String orderId;
    String orderName;
    String currency; // "KRW"
    String method; // 결제 수단
    String amount;
    String balanceAmount;
    String suppliedAmount;
    String vat; // 부가가치세
    String status; // 결제 처리 상태
    String requestedAt;
    String approvedAt;
    String useEscrow; // false
    String cultureExpense; // false
    PaymentSuccessCardResponse card; // 결제 카드 정보 (아래 자세한 정보 있음)
    String type; // 결제 타입 정보 (NOMAL / BILLING / CONNECTPAY)
    // 추가
    PaymentSuccessInfoResponse paymentSuccessInfoResponse;
}
