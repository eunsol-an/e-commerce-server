package kr.hhplus.be.server.domain.order.infrastructure.persistence.repository;

import io.lettuce.core.dynamic.annotation.Param;
import kr.hhplus.be.server.domain.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {
    @Query("""
        SELECT o.productId AS productId, SUM(o.quantity) AS totalQuantity
        FROM OrderItemJpaEntity o
        WHERE o.createdAt >= :threeDaysAgo
        GROUP BY o.productId
        ORDER BY SUM(o.quantity) DESC
        """)
    List<ProductQuantityProjection> findTop5ProductsLast3Days(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);

    interface ProductQuantityProjection {
        Long getProductId();
        Long getTotalQuantity();
    }
}
