package kr.hhplus.be.server.domain.product.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.application.ProductInfo;
import kr.hhplus.be.server.domain.product.application.ProductQuantity;

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
            @Schema(description = "총 판매 수량", example = "100")
            Long quantitySold
    ) {
        public static PopularProductResponse of(ProductQuantity productQuantity) {
            return new PopularProductResponse(
                    productQuantity.productId(),
                    productQuantity.quantity());
        }

        public static List<PopularProductResponse> of(List<ProductQuantity> productQuantities) {
            return productQuantities.stream()
                    .map(PopularProductResponse::of)
                    .toList();
        }
    }
}
