package kr.hhplus.be.server.domain.order.domain.event;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        List<OrderItem> items) {

    public static OrderCreatedEvent of(Order order) {
        return new OrderCreatedEvent(order.getId(), order.getUserId(), order.getItems());
    }
}

