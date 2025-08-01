package kr.hhplus.be.server.domain.order.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public Order toDomain(OrderJpaEntity entity) {
        if (entity == null) return null;

        return Order.of(
                entity.getId(),
                entity.getUserId(),
                entity.getCouponId(),
                entity.getItems().stream()
                        .map(this::domainMap)
                        .toList(),
                entity.getTotalItemPrice(),
                entity.getDiscountAmount()
        );
    }

    public OrderJpaEntity toEntity(Order domain) {
        if (domain == null) return null;

        OrderJpaEntity orderJpaEntity = new OrderJpaEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getCouponId(),
                null,
                domain.getTotalItemPrice(),
                domain.getDiscountAmount(),
                domain.getPaidAmount()
        );

        List<OrderItemJpaEntity> itemEntities = domain.getItems().stream()
                .map(item -> entityMap(item, orderJpaEntity)) // order 객체를 연결
                .toList();

        orderJpaEntity.setItems(itemEntities);
        return orderJpaEntity;
    }

    // 도메인 -> JPA
    public OrderItemJpaEntity entityMap(OrderItem item, OrderJpaEntity order) {
        if (item == null) return null;

        return new OrderItemJpaEntity(
                item.getId(),
                order, // 연관관계 주입
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
        );
    }

    // JPA -> 도메인
    public OrderItem domainMap(OrderItemJpaEntity entity) {
        if (entity == null) return null;

        return OrderItem.of(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getPrice()
        );
    }
}
