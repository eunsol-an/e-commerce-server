package kr.hhplus.be.server.domain.order.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem map(OrderItemJpaEntity entity);
    OrderItemJpaEntity map(OrderItem item);
}
