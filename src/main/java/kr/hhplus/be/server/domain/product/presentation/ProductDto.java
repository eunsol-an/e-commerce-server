package kr.hhplus.be.server.domain.product.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.application.ProductInfo;

import java.util.List;

public class ProductDto {
    @Schema(description = "상품 조회 Response")
    public record ProductResponse(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "상품명", example = "머쉬룸 스탠드")
            String name,
            @Schema(description = "상품 가격", example = "32000")
            Long price,
            @Schema(description = "재고량", example = "100")
            Integer stockQuantity
    ) {
        public static ProductResponse of(ProductInfo.Product product) {
            return new ProductResponse(
                    product.id(),
                    product.name(),
                    product.price(),
                    product.stockQuantity());
        }

        public static List<ProductDto.ProductResponse> of(List<ProductInfo.Product> products) {
            return products.stream()
                    .map(ProductResponse::of)
                    .toList();
        }
    }

    public record ProductListResponse(
            List<ProductResponse> products
    ) {
        public static ProductListResponse of(List<ProductResponse> products) {
            return new ProductListResponse(products);
        }
    }

    @Schema(description = "상위 상품 조회 Response")
    public record PopularProductResponse(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "상품명", example = "머쉬룸 스탠드")
            String name,
            @Schema(description = "총 판매 금액", example = "3200000")
            Long totalSales,
            @Schema(description = "총 판매 수량", example = "100")
            Integer quantitySold
    ) {}
}
