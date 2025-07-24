package kr.hhplus.be.server.domain.order.infrastructure.persistence;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.mapper.OrderMapper;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id)
                .map(orderMapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity orderJpaEntity = orderMapper.toEntity(order);
        return orderMapper.toDomain(orderJpaRepository.save(orderJpaEntity));
    }
}
