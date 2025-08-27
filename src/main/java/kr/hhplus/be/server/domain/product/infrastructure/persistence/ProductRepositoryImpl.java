package kr.hhplus.be.server.domain.product.infrastructure.persistence;

import kr.hhplus.be.server.domain.order.infrastructure.persistence.repository.OrderItemJpaRepository;
import kr.hhplus.be.server.domain.product.application.ProductQuantity;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.mapper.ProductMapper;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.mapper.ProductQuantityMapper;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final ProductMapper productMapper;
    private final ProductQuantityMapper productQuantityMapper;

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public Optional<Product> findByIdWithPessimisticLock(Long id) {
        return productJpaRepository.findByIdWithPessimisticLock(id)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findByIdIn(List<Long> ids) {
        return productJpaRepository.findByIdIn(ids)
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll()
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity productJpaEntity = productMapper.toEntity(product);
        return productMapper.toDomain(productJpaRepository.save(productJpaEntity));
    }

    @Override
    public void deleteAllInBatch() {
        productJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<ProductQuantity> findTop5ProductsLast3Days() {
        return orderItemJpaRepository.findTop5ProductsLast3Days(LocalDateTime.now().minusDays(3), PageRequest.of(0, 5))
                .stream()
                .map(projection
                        -> productQuantityMapper.toDomain(projection.getProductId(), projection.getTotalQuantity()))
                .toList();
    }
}
