package kr.hhplus.be.server.domain.point.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.point.infrastructure.persistence.entity.UserPointJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<UserPointJpaEntity, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM UserPointJpaEntity p WHERE p.id = :id")
    Optional<UserPointJpaEntity> findByIdWithOptimisticLock(Long id);
}
