package kr.hhplus.be.server.domain.product.application;

import kr.hhplus.be.server.domain.order.application.OrderCommand;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.exception.ErrorCode.OUT_OF_STOCK;
import static kr.hhplus.be.server.exception.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductInfo.Product> getAllProducts() {
        return ProductInfo.Product.of(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductInfo.Product getProduct(Long productId) {
        return ProductInfo.Product.of(productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(PRODUCT_NOT_FOUND)));
    }

    public Product validateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(PRODUCT_NOT_FOUND));

        if (!product.isAvailable(quantity)) {
            throw new ApiException(OUT_OF_STOCK);
        }
        return product;
    }

    public List<Product> validateStocks(List<OrderCommand.Item> items) {
        return items.stream()
                .map(itemCommand -> validateStock(itemCommand.productId(), itemCommand.quantity()))
                .toList();
    }

//    @Transactional
    public void deductStock(List<OrderCommand.Item> items) {
        items.forEach(item -> {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ApiException(PRODUCT_NOT_FOUND));

            product.deductStock(item.quantity());
            productRepository.save(product);
        });
    }
}
