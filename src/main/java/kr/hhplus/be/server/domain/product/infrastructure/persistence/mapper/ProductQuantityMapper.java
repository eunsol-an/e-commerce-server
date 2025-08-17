package kr.hhplus.be.server.domain.product.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.product.application.ProductQuantity;
import org.springframework.stereotype.Component;

@Component
public class ProductQuantityMapper {
    public ProductQuantity toDomain(Long productId, Long quantity) {
        if (productId == null || quantity == null) return null;
        return ProductQuantity.of(productId, quantity);
    }
}
