package kr.hhplus.be.server.domain.product.infrastructure.persistence;

import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.entity.ProductJpaEntity;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.mapper.ProductMapper;
import kr.hhplus.be.server.domain.product.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;

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
}
