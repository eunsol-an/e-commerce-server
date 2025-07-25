package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderInfo.Order save(Order newOrder) {
        Order save = orderRepository.save(newOrder);
        return OrderInfo.Order.of(save);
    }
}
