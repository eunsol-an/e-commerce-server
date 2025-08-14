package kr.hhplus.be.server.domain.product.infrastructure.persistence.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductJpaEntity AS p WHERE p.id = :id")
    Optional<ProductJpaEntity> findByIdWithPessimisticLock(Long id);

    @Query("""
        SELECT o.productId AS productId, SUM(o.quantity) AS totalQuantity
        FROM OrderItemJpaEntity o
        WHERE o.createdAt >= :threeDaysAgo
        GROUP BY o.productId
        ORDER BY totalQuantity DESC
        """)
    List<ProductQuantityProjection> findTop5ProductsLast3Days(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);

    interface ProductQuantityProjection {
        Long getProductId();
        Long getTotalQuantity();
    }
}
