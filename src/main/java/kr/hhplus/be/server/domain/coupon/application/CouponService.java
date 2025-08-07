package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    public Coupon getCouponByUser(Long couponId, Long userId) {
        return couponRepository.findByIdAndUserIdAndStatusIssued(couponId, userId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
    }

    public CouponPolicy getCouponPolicy(Long couponPolicyId) {
        return couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new ApiException(COUPON_POLICY_NOT_FOUND));
    }

    @Transactional
    public long applyCoupon(Long couponId, Long userId) {
        Coupon coupon = getCouponByUser(couponId, userId);
        validateCouponUsable(coupon);
        coupon.use();

        CouponPolicy couponPolicy = getCouponPolicy(coupon.getCouponPolicyId());
        couponRepository.save(coupon);
        return couponPolicy.getDiscountAmount();
    }

    @Transactional
    public void issue(Long userId, Long couponPolicyId) {
        // 1. 쿠폰 정책 조회 (없으면 예외 발생)
        CouponPolicy couponPolicy = couponPolicyRepository.findByIdWithPessimisticLock(couponPolicyId)
                .orElseThrow(() -> new ApiException(COUPON_POLICY_NOT_FOUND));

        // 2. 쿠폰 발급 수량 초과 여부 확인
        if (!couponPolicy.isIssuable()) {
            throw new ApiException(COUPON_SOLD_OUT);
        }

        // 3. 이미 해당 유저가 이 쿠폰 정책으로 쿠폰을 발급받았는지 확인 (중복 발급 방지)
        boolean alreadyUsed = couponRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicy.getId());
        if (alreadyUsed) {
            throw new ApiException(ALREADY_ISSUED_COUPON);
        }

        // 4. 쿠폰 생성 및 저장
        Coupon coupon = Coupon.create(userId, couponPolicyId, CouponStatus.ISSUED);
        couponRepository.save(coupon);

        // 5. 쿠폰 발급 수량 1 증가
        couponPolicy.increaseIssuedCount();
        couponPolicyRepository.save(couponPolicy);
    }

    private void validateCouponUsable(Coupon coupon) {
        if (!coupon.isAvailable()) {
            throw new ApiException(COUPON_INVALID_STATUS);
        }
    }
}
