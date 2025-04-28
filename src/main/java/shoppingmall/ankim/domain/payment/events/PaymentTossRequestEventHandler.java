package shoppingmall.ankim.domain.payment.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentTossRequestEventHandler {

    private final PaymentService paymentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentTossRequestEvent event) {
        log.info("[PaymentTossRequestEventHandler] Toss 결제 준비 시작");

        // Toss 결제 요청
        paymentService.requestTossPayment(event.getPaymentRequest());

        log.info("[PaymentTossRequestEventHandler] Toss 결제 준비 완료");
    }
}
