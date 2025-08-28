package kr.hhplus.be.server.domain.order.domain.event;

import kr.hhplus.be.server.domain.order.domain.model.OrderItem;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        List<OrderItem> items) {
}

