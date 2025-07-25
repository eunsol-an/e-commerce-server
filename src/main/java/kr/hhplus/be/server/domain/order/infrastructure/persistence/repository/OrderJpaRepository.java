package kr.hhplus.be.server.domain.order.infrastructure.persistence.repository;

import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
}
