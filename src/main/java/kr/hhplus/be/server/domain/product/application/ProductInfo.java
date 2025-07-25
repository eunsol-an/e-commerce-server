package kr.hhplus.be.server.domain.product.application;

import java.util.List;

public class ProductInfo {
    public record Product(
            Long id,
            String name,
            Long price,
            Integer stockQuantity
    ) {
        public static Product of(kr.hhplus.be.server.domain.product.domain.model.Product product) {
            return new Product(product.getId(), product.getName(), product.getPrice(), product.getStockQuantity());
        }

        public static List<Product> of(List<kr.hhplus.be.server.domain.product.domain.model.Product> products) {
            return products.stream()
                    .map(Product::of)
                    .toList();
        }
    }
}
