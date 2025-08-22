package kr.hhplus.be.server.domain.coupon.infrastructure.redis;

import kr.hhplus.be.server.common.cache.RedisKeys;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponMemoryRepository;
import kr.hhplus.be.server.domain.coupon.infrastructure.redis.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponMemoryRepositoryImpl implements CouponMemoryRepository {
    private final CouponRedisRepository couponRedisRepository;

    /**
     * 쿠폰 중복 발급 확인
     */
    @Override
    public boolean isUserIssued(Long couponPolicyId, Long userId) {
        return couponRedisRepository.isUserIssued(RedisKeys.COUPON_USERS.format(couponPolicyId), userId);
    }

    /**
     * 쿠폰 수량 확인
     */
    @Override
    public boolean decreaseStock(Long couponPolicyId, Long userId) {
        Long remain = couponRedisRepository.decreaseStock(RedisKeys.COUPON_STOCK.format(couponPolicyId));
        return remain != null && remain >= 0;
    }

    /**
     * 발급 확정 처리
     */
    @Override
    public void registerIssue(Long couponPolicyId, Long userId) {
        couponRedisRepository.addIssuedUser(RedisKeys.COUPON_USERS.format(couponPolicyId), userId);
    }

    /**
     * 대기열 추가
     */
    @Override
    public void enqueue(Long couponPolicyId, Long userId) {
        couponRedisRepository.enqueue(RedisKeys.COUPON_QUEUE.format(couponPolicyId), userId, System.currentTimeMillis());
    }

    /**
     * 대기열에서 발급자 추출
     */
    @Override
    public List<Long> dequeue(Long couponPolicyId, int size) {
        return couponRedisRepository.dequeue(RedisKeys.COUPON_QUEUE.format(couponPolicyId), size);
    }
}
