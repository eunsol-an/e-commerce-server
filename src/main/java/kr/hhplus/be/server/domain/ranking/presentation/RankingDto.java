package kr.hhplus.be.server.domain.ranking.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.ranking.application.RankedProductInfo;

import java.util.List;

public class RankingDto {

    @Schema(description = "상위 상품 조회 Response")
    public record PopularProductResponse(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "상품 이름", example = "머쉬룸 스탠드")
            String name,
            @Schema(description = "총 판매 수량", example = "100")
            Long price
    ) {
        public static RankingDto.PopularProductResponse of(RankedProductInfo.RankedProduct rankedProduct) {
            return new RankingDto.PopularProductResponse(
                    rankedProduct.id(),
                    rankedProduct.name(),
                    rankedProduct.price());
        }

        public static List<RankingDto.PopularProductResponse> of(List<RankedProductInfo.RankedProduct> rankedProducts) {
            return rankedProducts.stream()
                    .map(RankingDto.PopularProductResponse::of)
                    .toList();
        }
    }
}
