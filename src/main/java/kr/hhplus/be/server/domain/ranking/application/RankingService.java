package kr.hhplus.be.server.domain.ranking.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.cache.RedisKeys;
import kr.hhplus.be.server.common.cache.RedisCacheRepository;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.domain.ranking.domain.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ObjectMapper objectMapper;

    private final RankingRepository rankingRepository;
    private final ProductRepository productRepository;
    private final RedisCacheRepository redisCacheRepository;


    public void increaseScore(Long productId, int count) {
        rankingRepository.increaseScore(productId, count);
    }

    @Transactional(readOnly = true)
    public List<RankedProductInfo.RankedProduct> getTop5ProductsLast3Days() {
        // 1. 캐시 확인 (Look-Aside 전략)
        String cachedJson = redisCacheRepository.get(RedisKeys.PRODUCT_RANKING_3DAYS);
        if (cachedJson != null) {
            return deserialize(cachedJson);
        }

        // 2. 최근 3일간 랭킹 합산
        List<Long> productIds = rankingRepository.getTop5ProductsLast3Days();

        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. ProductRepository 통해 DB 조회
        List<Product> products = productRepository.findByIdIn(productIds);

        // 4. 캐시에 저장 (TTL: 10분)
        String json = serialize(products);
        redisCacheRepository.set(RedisKeys.PRODUCT_RANKING_3DAYS, json);

        return RankedProductInfo.RankedProduct.of(products);
    }

    /**
     * 캐시 강제 갱신 (배치, 이벤트 시 사용)
     */
    @Transactional
    public void refreshTop5ProductsCache() {
        List<Long> productIds = rankingRepository.getTop5ProductsLast3Days();
        List<Product> products = productRepository.findByIdIn(productIds);
        String json = serialize(products);
        redisCacheRepository.set(RedisKeys.PRODUCT_RANKING_3DAYS, json);
    }

    /**
     * 캐시 삭제 (이벤트 발생 시 사용)
     */
    public void evictTop5ProductsCache() {
        redisCacheRepository.delete(RedisKeys.PRODUCT_RANKING_3DAYS);
    }


    private List<RankedProductInfo.RankedProduct> deserialize(String json) {
        try {
            return Arrays.asList(objectMapper.readValue(json, RankedProductInfo.RankedProduct[].class));
        } catch (Exception e) {
            throw new RuntimeException("Redis deserialize error", e);
        }
    }

    private String serialize(List<Product> products) {
        try {
            return objectMapper.writeValueAsString(products);
        } catch (Exception e) {
            throw new RuntimeException("Redis serialize error", e);
        }
    }
}
