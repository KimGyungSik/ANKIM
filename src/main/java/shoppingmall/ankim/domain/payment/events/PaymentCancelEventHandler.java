package shoppingmall.ankim.domain.payment.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.order.events.OrderCanceledEvent;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.global.config.TossPaymentConfig;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.PAYMENT_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCancelEventHandler {
    private final PaymentService paymentService;
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(OrderCanceledEvent event) {
        log.info("[PaymentCancelEventHandler] 주문 취소 → 결제 취소 진행");
        paymentService.cancelPayment(event.getOrderId(), event.getCancelReason());
    }
}

