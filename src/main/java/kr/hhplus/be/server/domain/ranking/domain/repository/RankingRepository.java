package kr.hhplus.be.server.domain.ranking.domain.repository;

import java.util.List;

public interface RankingRepository {
    void increaseScore(Long productId, int count);
    List<Long> getTop5ProductsLast3Days();
    void clearRanking();
}
