package kr.hhplus.be.server.domain.coupon.domain.repository;

import java.util.List;

public interface CouponMemoryRepository {
    boolean isUserIssued(Long couponPolicyId, Long userId);
    boolean decreaseStock(Long couponPolicyId, Long userId);
    void registerIssue(Long couponPolicyId, Long userId);
    void enqueue(Long couponPolicyId, Long userId);
    List<Long> dequeue(Long couponPolicyId, int size);
}
