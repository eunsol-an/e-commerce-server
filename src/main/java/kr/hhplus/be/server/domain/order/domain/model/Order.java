package kr.hhplus.be.server.domain.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Order {
    private Long id;
    private Long userId;
    private Long couponId;
    private List<OrderItem> items;
    private long totalItemPrice;
    private long discountAmount;
    private long paidAmount;

    @Builder
    private Order(Long userId, Long couponId, List<OrderItem> items, long totalItemPrice, long discountAmount) {
        this.id = null;
        this.userId = userId;
        this.couponId = couponId;
        this.items = items;
        this.totalItemPrice = totalItemPrice;
        this.discountAmount = discountAmount;
        this.paidAmount = totalItemPrice - discountAmount;
    }

    public static Order create(Long userId, Long couponId, List<OrderItem> items) {
        return Order.builder()
                .userId(userId)
                .couponId(couponId)
                .items(items)
                .totalItemPrice(calculateTotalItemAmount(items))
                .build();
    }

    private static long calculateTotalItemAmount(List<OrderItem> items) {
        return items.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void applyDiscount(long discountAmount) {
        this.discountAmount = discountAmount;
        this.paidAmount = totalItemPrice - discountAmount;
    }
}
