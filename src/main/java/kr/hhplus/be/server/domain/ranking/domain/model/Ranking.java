package kr.hhplus.be.server.domain.ranking.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Ranking {
    private Long productId;
    private Double score;
    private Long rank;

    @Builder
    public Ranking(Long productId, Double score, Long rank) {
        this.productId = productId;
        this.score = score;
        this.rank = rank;
    }
}
