package shoppingmall.ankim.factory;

import jakarta.persistence.EntityManager;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;

import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.payment.entity.PayType.*;

public class PaymentFactory {

    public static List<Payment> createPayments(EntityManager em, int count) {
        List<Order> orders = OrderFactory.createOrders(em, count);

        return orders.stream()
                .map(order -> Payment.create(order, CARD, order.getTotalPrice()))
                .toList();
    }
}
