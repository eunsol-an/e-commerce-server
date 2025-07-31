package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;

import java.util.List;

public class OrderInfo {
    public record Order(
            Long orderId,
            Long userId,
            Long couponPolicyId,
            List<OrderItem> items,
            long totalItemPrice,
            long discountAmount,
            long paidAmount
    ) {
        public static OrderInfo.Order of(kr.hhplus.be.server.domain.order.domain.model.Order order) {
            return new OrderInfo.Order(
                    order.getId(),
                    order.getUserId(),
                    order.getCouponPolicyId(),
                    order.getItems(),
                    order.getTotalItemPrice(),
                    order.getDiscountAmount(),
                    order.getPaidAmount()
            );
        }
    }
}
