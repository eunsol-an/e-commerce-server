package kr.hhplus.be.server.domain.coupon.domain.repository;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;

import java.util.Optional;

public interface CouponPolicyRepository {
    Optional<CouponPolicy> findById(Long userId);
}
