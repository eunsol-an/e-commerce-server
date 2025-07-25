package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.exception.ErrorCode.COUPON_INVALID_STATUS;
import static kr.hhplus.be.server.exception.ErrorCode.COUPON_NOT_FOUNT;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    public Coupon validate(Long couponPolicyId, Long userId) {
        Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(couponPolicyId, userId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
        if (!coupon.isAvailable()) {
            throw new ApiException(COUPON_INVALID_STATUS);
        }
        return coupon;
    }

    public CouponPolicy getCouponPolicy(Long couponId) {
        return couponPolicyRepository.findById(couponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
    }

    public void use(Long couponPolicyId, Long userId) {
        Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(couponPolicyId, userId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
        coupon.use();
        couponRepository.save(coupon);
    }
}
