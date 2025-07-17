package kr.hhplus.be.server.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

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
    ) {}

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
