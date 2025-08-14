package kr.hhplus.be.server.domain.product.application;

public record ProductQuantity(Long productId, Long quantity) {
    public static ProductQuantity of(Long productId, Long quantity) {
        return new ProductQuantity(productId, quantity);
    }
}
