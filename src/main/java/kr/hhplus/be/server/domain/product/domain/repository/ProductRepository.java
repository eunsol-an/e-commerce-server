package kr.hhplus.be.server.domain.product.domain.repository;

import kr.hhplus.be.server.domain.product.application.ProductQuantity;
import kr.hhplus.be.server.domain.product.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    Optional<Product> findByIdWithPessimisticLock(Long id);
    List<Product> findByIdIn(List<Long> ids);
    List<Product> findAll();
    Product save(Product product);
    void deleteAllInBatch();
    List<ProductQuantity> findTop5ProductsLast3Days();
}
