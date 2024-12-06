package shoppingmall.ankim.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;

import static shoppingmall.ankim.global.exception.ErrorCode.ORDER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

//    private final OrderRepository orderRepository;
//    public Order findOrder(String orderName) {
//        return orderRepository.findByOrderName(orderName)
//                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
//    }

}
