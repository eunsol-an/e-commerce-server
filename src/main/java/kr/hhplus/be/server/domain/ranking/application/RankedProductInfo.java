package kr.hhplus.be.server.domain.ranking.application;

import kr.hhplus.be.server.domain.product.domain.model.Product;

import java.util.List;

public record RankedProductInfo() {
    public record RankedProduct(
            Long id,
            String name,
            Long price
    ) {
        public static RankedProduct of(Product product) {
            return new RankedProduct(product.getId(), product.getName(), product.getPrice());
        }

        public static List<RankedProduct> of(List<Product> products) {
            return products.stream()
                    .map(RankedProduct::of)
                    .toList();
        }
    }
}
