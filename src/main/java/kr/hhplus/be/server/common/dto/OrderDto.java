package kr.hhplus.be.server.common.dto;

import java.util.List;

public class OrderDto {
    public record OrderRequest(
            Long userId,
            List<OrderItemRequest> items,
            Long couponId // nullable
    ) {}

    public record OrderResponse(
            Long orderId,
            Long totalItemPrice,
            Long discountAmount,
            Long paidAmount,
            List<OrderItemResponse> items
    ) {}

    public record OrderItemRequest(
            Long productId,
            Integer quantity
    ) {}

    public record OrderItemResponse (
            Long productId,
            String name,
            Long price,
            Integer quantity
    ) {

    }
}
