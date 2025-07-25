package kr.hhplus.be.server.domain.product.domain.repository;

import kr.hhplus.be.server.domain.product.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    List<Product> findAll();
    Product save(Product product);
}
