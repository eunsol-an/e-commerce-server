package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.presentation.OrderDto;

import java.util.ArrayList;
import java.util.List;

public class OrderCommand {
    public record Create(
            Long userId,
            List<OrderCommand.Item> items,
            Long couponId
    ) {
        public static Create of(Long userId, List<OrderDto.OrderItemRequest> items, Long couponId) {
            List<OrderCommand.Item> itemsList = new ArrayList<>();
            for (OrderDto.OrderItemRequest item : items) {
                itemsList.add(OrderCommand.Item.of(item.productId(), item.quantity()));
            }
            return new Create(userId, itemsList, couponId);
        }
    }

    public record Item(
            Long productId,
            int quantity
    ) {
        public static Item of(Long productId, int quantity) {
            return new Item(productId, quantity);
        }
    }
}
