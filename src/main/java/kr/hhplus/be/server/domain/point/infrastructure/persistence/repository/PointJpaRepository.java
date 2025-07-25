package kr.hhplus.be.server.domain.point.infrastructure.persistence.repository;

import kr.hhplus.be.server.domain.point.infrastructure.persistence.entity.UserPointJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointJpaRepository extends JpaRepository<UserPointJpaEntity, Long> {
}
