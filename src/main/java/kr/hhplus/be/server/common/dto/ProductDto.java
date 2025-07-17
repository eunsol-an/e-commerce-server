package kr.hhplus.be.server.common.dto;

public class ProductDto {
    public record ProductResponse(
            Long productId,
            String name,
            Long price,
            Integer stockQuantity
    ) {}

    public record PopularProductResponse(
            Long productId,
            String name,
            Long totalSales,
            Integer quantitySold
    ) {}
}
