package kr.hhplus.be.server.domain.coupon.domain.repository;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;

import java.util.Optional;

public interface CouponPolicyRepository {
    Optional<CouponPolicy> findById(Long couponPolicyId);
    Optional<CouponPolicy> findByIdWithPessimisticLock(Long couponPolicyId);
    CouponPolicy save(CouponPolicy couponPolicy);
    int tryIncreaseIssuedCount(Long couponPolicyId);
    void deleteAllInBatch();
}
