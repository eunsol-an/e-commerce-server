package kr.hhplus.be.server.domain.ranking.infrastructure.redis;

import kr.hhplus.be.server.domain.ranking.domain.repository.RankingRepository;
import kr.hhplus.be.server.domain.ranking.infrastructure.redis.repository.RedisRakingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {
    private final RedisRakingRepository redisRakingRepository;

    @Override
    public void increaseScore(Long productId, int count) {
        redisRakingRepository.increaseScore(productId, count);
    }

    @Override
    public List<Long> getTop5ProductsLast3Days() {
        return redisRakingRepository.getTop5ProductsLast3Days();
    }

    @Override
    public void clearRanking() {
        redisRakingRepository.clearRanking();
    }
}
