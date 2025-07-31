package kr.hhplus.be.server.domain.coupon.domain.repository;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByIdAndUserId(Long couponPolicyId, Long userId);
    Coupon save(Coupon coupon);
    boolean existsByUserIdAndCouponPolicyId(Long userId, Long couponPolicyId);
}