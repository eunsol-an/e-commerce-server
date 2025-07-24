package kr.hhplus.be.server.domain.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Order {
    private Long id;
    private Long userId;
    private Long couponPolicyId;
    private List<OrderItem> items;
    private long totalItemPrice;
    private long discountAmount;
    private long paidAmount;

    @Builder
    private Order(Long userId, Long couponPolicyId, List<OrderItem> items, long totalItemPrice, long discountAmount) {
        this.id = null;
        this.userId = userId;
        this.couponPolicyId = couponPolicyId;
        this.items = items;
        this.totalItemPrice = totalItemPrice;
        this.discountAmount = discountAmount;
        this.paidAmount = totalItemPrice - discountAmount;
    }

    public static Order create(Long userId, Long couponPolicyId, List<OrderItem> items, long totalItemPrice, long discountAmount) {
        return Order.builder()
                .userId(userId)
                .couponPolicyId(couponPolicyId)
                .items(items != null ? items : new ArrayList<>())
                .totalItemPrice(totalItemPrice)
                .discountAmount(discountAmount)
                .build();
    }
}
