package kr.hhplus.be.server.domain.product.infrastructure.persistence.repository;

import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
}
