package kr.hhplus.be.server.domain.product.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toDomain(ProductJpaEntity entity);
    ProductJpaEntity toEntity(Product domain);
}
