package kr.hhplus.be.server.domain.ranking.infrastructure.redis.repository;

import kr.hhplus.be.server.common.cache.CacheKey;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRakingRepository {
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    /**
     * 상품 주문 발생 시 점수 증가
     */
    public void increaseScore(Long productId, int count) {
        String key = applySuffix(LocalDate.now());

        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(key);

        // 1. 점수 증가 (없으면 추가)
        zSet.addScore(productId.toString(), count);

        // 2. TTL 설정 (key가 새로 만들어진 경우만 TTL 적용)
        if (!zSet.isExists()) {
            zSet.expire(CacheKey.PRODUCT_RANKING.getTtl());
        }
    }

    /**
     * Top 5 상품 가져오기
     */
    public List<Long> getTop5ProductsLast3Days() {
        LocalDate today = LocalDate.now();

        String d0 = applySuffix(today);
        String d1 = applySuffix(today.minusDays(1));
        String d2 = applySuffix(today.minusDays(2));

        // ZUNIONSTORE
        ZSetOperations<String, String> zops = redisTemplate.opsForZSet();
        zops.unionAndStore(d0, Arrays.asList(d1, d2), CacheKey.PRODUCT_RANKING_3DAYS.getKey());

        // 집계 결과 TTL 설정 (집계 주기에 맞춰 짧게 유지)
        redisTemplate.expire(CacheKey.PRODUCT_RANKING_3DAYS.getKey(), CacheKey.PRODUCT_RANKING_3DAYS.getTtl());

        Set<String> ids = redisTemplate.opsForZSet().reverseRange(CacheKey.PRODUCT_RANKING_3DAYS.getKey(), 0, 4);
        if (ids == null || ids.isEmpty()) return List.of();
        return ids.stream().map(Long::valueOf).toList();
    }

    /**
     * 랭킹 초기화 (테스트용 / 이벤트 종료 후 초기화용)
     */
    public void clearRanking() {
        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(CacheKey.PRODUCT_RANKING.getKey());

        // 전체 초기화
        zSet.delete();

    }

    /**
     * Suffix 적용
     */
    private String applySuffix(LocalDate date) {
        return CacheKey.PRODUCT_RANKING.getKey() + ":" + date.toString();
    }
}
