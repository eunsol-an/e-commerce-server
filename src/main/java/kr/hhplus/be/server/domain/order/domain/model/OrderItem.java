package kr.hhplus.be.server.domain.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private long price;
    private LocalDateTime createdAt;

    @Builder
    private OrderItem(Long id, Long orderId, Long productId, int quantity, long price) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItem of(Long orderId, Long productId, int quantity, long price) {
        return OrderItem.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
    }

    public static OrderItem create(Long productId, int quantity, long price) {
        return OrderItem.builder()
                .orderId(null)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
