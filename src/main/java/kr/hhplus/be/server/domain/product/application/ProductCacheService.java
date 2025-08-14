package kr.hhplus.be.server.domain.product.application;

import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCacheService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "cache:product:top5:last3days";
    private static final Duration TTL = Duration.ofDays(1);

    @Transactional(readOnly = true)
    public List<ProductQuantity> getTop5ProductsLast3Days() {
        // 1. 캐시 확인 (Look-Aside 전략)
        List<ProductQuantity> cachedData = (List<ProductQuantity>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cachedData != null) {
            return cachedData;
        }

        // 2. DB 조회
        List<ProductQuantity> freshData = productRepository.findTop5ProductsLast3Days();

        // 3. 캐시에 저장 (TTL: 1일)
        redisTemplate.opsForValue().set(CACHE_KEY, freshData, TTL);

        return freshData;
    }

    /**
     * 캐시 강제 갱신 (배치, 이벤트 시 사용)
     */
    @Transactional
    public void refreshTop5ProductsCache() {
        List<ProductQuantity> freshData = productRepository.findTop5ProductsLast3Days();
        redisTemplate.opsForValue().set(CACHE_KEY, freshData, TTL);
    }

    /**
     * 캐시 삭제 (이벤트 발생 시 사용)
     */
    public void evictTop5ProductsCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}
