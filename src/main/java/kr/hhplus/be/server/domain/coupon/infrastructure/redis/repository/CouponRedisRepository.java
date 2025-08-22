package kr.hhplus.be.server.domain.coupon.infrastructure.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {
    private final StringRedisTemplate redisTemplate;

    // 발급 여부 확인
    public Boolean isUserIssued(String key, Long userId) {
        return redisTemplate.opsForSet().isMember(key, userId.toString());
    }

    // 수량 차감
    public Long decreaseStock(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // 발급자 등록
    public void addIssuedUser(String key, Long userId) {
        redisTemplate.opsForSet().add(key, userId.toString());
    }

    // 대기열 추가 (score = timestamp)
    public void enqueue(String queueKey, Long userId, long timestamp) {
        redisTemplate.opsForZSet().add(queueKey, userId.toString(), timestamp);
    }

    // 대기열에서 발급자 추출
    public List<Long> dequeue(String queueKey, int size) {
        var users = redisTemplate.opsForZSet().popMin(queueKey, size);
        if (users == null) return List.of();
        return users.stream()
                .map(tuple -> Long.valueOf(Objects.requireNonNull(tuple.getValue())))
                .toList();
    }
}
