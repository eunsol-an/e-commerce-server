package kr.hhplus.be.server.domain.product.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;
        return Product.of(entity.getId(), entity.getName(), entity.getPrice(), entity.getStockQuantity());
    }
    public ProductJpaEntity toEntity(Product domain) {
        if (domain == null) return null;
        return new ProductJpaEntity(domain.getId(), domain.getName(), domain.getPrice(), domain.getStockQuantity());
    }
}
