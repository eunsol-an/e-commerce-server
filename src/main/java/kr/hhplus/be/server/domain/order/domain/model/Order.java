package kr.hhplus.be.server.domain.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    @Builder
    private Order(Long id, Long userId, Long couponId, List<OrderItem> items, long totalItemPrice, long discountAmount) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.items = items;
        this.totalItemPrice = totalItemPrice;
        this.discountAmount = discountAmount;
        this.paidAmount = totalItemPrice - discountAmount;
    }

    public static Order of(Long id, Long userId, Long couponId, List<OrderItem> items, long totalItemPrice, long discountAmount) {
        return Order.builder()
                .id(id)
                .userId(userId)
                .couponId(couponId)
                .items(items)
                .totalItemPrice(totalItemPrice)
                .discountAmount(discountAmount)
                .build();
    }

    public static Order create(Long userId, Long couponId, List<OrderItem> items) {
        return Order.builder()
                .id(null)
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
