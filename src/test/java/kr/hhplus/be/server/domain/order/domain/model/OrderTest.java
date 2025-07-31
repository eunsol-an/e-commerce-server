package kr.hhplus.be.server.domain.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order 도메인 테스트")
class OrderTest {

    @Test
    @DisplayName("정상적으로 주문이 생성된다")
    void 주문생성() {
        Long orderId = 1L;
        Long userId = 100L;

        OrderItem item1 = OrderItem.of(orderId, 101L, 2, 5000); // 10,000
        OrderItem item2 = OrderItem.of(orderId, 102L, 1, 15000); // 15,000
        OrderItem item3 = OrderItem.of(orderId, 103L, 3, 3000); // 9,000

        List<OrderItem> items = List.of(item1, item2, item3);

        Order order = Order.create(userId, 3000L, items);

        assertEquals(34000L, order.getPaidAmount());
    }

    @Test
    @DisplayName("할인이 정상적으로 적용된다")
    void 할인_적용() {
        Long orderId = 1L;
        Long userId = 100L;
        long discountAmount = 1000L;

        OrderItem item1 = OrderItem.of(orderId, 101L, 2, 5000); // 10,000
        OrderItem item2 = OrderItem.of(orderId, 102L, 1, 15000); // 15,000
        OrderItem item3 = OrderItem.of(orderId, 103L, 3, 3000); // 9,000

        List<OrderItem> items = List.of(item1, item2, item3);

        Order order = Order.create(userId, 3000L, items);
        order.applyDiscount(discountAmount);

        assertEquals(33000L, order.getPaidAmount());
    }
}