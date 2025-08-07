package kr.hhplus.be.server.domain.product.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductJpaEntity AS p WHERE p.id = :id")
    Optional<ProductJpaEntity> findByIdWithPessimisticLock(Long id);
}
