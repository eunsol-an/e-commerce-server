package kr.hhplus.be.server.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisCacheRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "cache:";

    /**
     * 캐시 저장 (enum 사용, TTL 지정 가능)
     */
    public void set(CacheKey cacheKey, String value, Duration ttl) {
        redisTemplate.opsForValue().set(applyPrefix(cacheKey.getKey()), value, ttl);
    }

    /**
     * 캐시 저장 (enum 사용, enum의 TTL 사용)
     */
    public void set(CacheKey cacheKey, String value) {
        set(cacheKey, value, cacheKey.getTtl());
    }

    /**
     * 캐시 조회
     */
    public String get(CacheKey cacheKey) {
        return redisTemplate.opsForValue().get(applyPrefix(cacheKey.getKey()));
    }

    /**
     * 캐시 삭제
     */
    public void delete(CacheKey cacheKey) {
        redisTemplate.delete(applyPrefix(cacheKey.getKey()));
    }

    /**
     * Prefix 적용
     */
    private String applyPrefix(String key) {
        return PREFIX + key;
    }
}
