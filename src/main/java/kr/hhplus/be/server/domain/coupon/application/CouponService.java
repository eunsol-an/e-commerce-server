package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponMemoryRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponMemoryRepository couponMemoryRepository;

    public Coupon getCouponByUser(Long couponId, Long userId) {
        return couponRepository.findByIdAndUserIdAndStatusIssued(couponId, userId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
    }

    public CouponPolicy getCouponPolicy(Long couponPolicyId) {
        return couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new ApiException(COUPON_POLICY_NOT_FOUND));
    }

//    @Transactional
    public long applyCoupon(Long couponId, Long userId) {
        Coupon coupon = getCouponByUser(couponId, userId);
        validateCouponUsable(coupon);
        coupon.use();

        CouponPolicy couponPolicy = getCouponPolicy(coupon.getCouponPolicyId());
        couponRepository.save(coupon);
        return couponPolicy.getDiscountAmount();
    }

//    @Transactional
//    @DistributedLock(
//            prefix = "coupon",
//            keys = {"#couponPolicyId"},
//            fair = true,
//            waitTime = 5,
//            leaseTime = 2
//    )
    public boolean issue(Long userId, Long couponPolicyId) {
        // 1. 이미 해당 유저가 이 쿠폰 정책으로 쿠폰을 발급받았는지 확인 (중복 발급 방지)
        if (!couponMemoryRepository.isUserIssued(couponPolicyId, userId)) {
            throw new ApiException(ALREADY_ISSUED_COUPON);
        }

        // 2. 쿠폰 발급 수량 차감
        if (!couponMemoryRepository.decreaseStock(couponPolicyId, userId)) {
            throw new ApiException(COUPON_SOLD_OUT);
        }

        // 3. 유저 Set 등록 (중복 발급 방지)
        couponMemoryRepository.registerIssue(couponPolicyId, userId);

        // 4. Sorted Set 대기열에 추가 (score = timestamp)
        couponMemoryRepository.enqueue(couponPolicyId, userId);

        // 5. DB 반영은 스케줄러/비동기로 처리
        return true;
    }

    private void validateCouponUsable(Coupon coupon) {
        if (!coupon.isAvailable()) {
            throw new ApiException(COUPON_INVALID_STATUS);
        }
    }
}
