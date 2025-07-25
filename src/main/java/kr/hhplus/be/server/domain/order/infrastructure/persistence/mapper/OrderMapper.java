package kr.hhplus.be.server.domain.order.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toDomain(OrderJpaEntity entity);
    OrderJpaEntity toEntity(Order domain);
    @Mapping(target = "order", ignore = true)
    OrderItemJpaEntity map(OrderItem item);
    @Mapping(target = "orderId", source = "order.id")
    OrderItem map(OrderItemJpaEntity entity);
}
