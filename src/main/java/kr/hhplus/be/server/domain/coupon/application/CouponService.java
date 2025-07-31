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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static kr.hhplus.be.server.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    private final Map<Long, Object> couponLocks = new ConcurrentHashMap<>();

    public Coupon validate(Long couponPolicyId, Long userId) {
        Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(couponPolicyId, userId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
        if (!coupon.isAvailable()) {
            throw new ApiException(COUPON_INVALID_STATUS);
        }
        return coupon;
    }

    public CouponPolicy getCouponPolicy(Long couponPolicyId) {
        return couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new ApiException(COUPON_POLICY_NOT_FOUND));
    }

    public void use(Long couponPolicyId, Long userId) {
        Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(couponPolicyId, userId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_FOUNT));
        coupon.use();
        couponRepository.save(coupon);
    }

    public long applyCoupon(Long couponPolicyId, Long userId) {
        Coupon coupon = validate(couponPolicyId, userId);
        CouponPolicy couponPolicy = getCouponPolicy(coupon.getCouponPolicyId());
        use(couponPolicyId, userId);
        return couponPolicy.getDiscountAmount();
    }

    @Transactional
    public void issue(Long userId, Long couponPolicyId) {
        // 1. couponPolicyId 기준으로 락 객체를 가져오거나 새로 생성
        //    - 동시성 문제를 피하기 위해 메모리 기반 동기화 처리
        Object lock = couponLocks.computeIfAbsent(couponPolicyId, k -> new Object());

        // 2. 동기화 블록 진입 (동일 쿠폰 정책에 대해 하나의 쓰레드만 접근 가능)
        synchronized (lock) {

            // 3. 쿠폰 정책 조회 (없으면 예외 발생)
            CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId)
                    .orElseThrow(() -> new ApiException(COUPON_POLICY_NOT_FOUND));

            // 4. 쿠폰 발급 수량 초과 여부 확인
            if (!couponPolicy.isIssuable()) {
                throw new ApiException(COUPON_SOLD_OUT);
            }

            // 5. 이미 해당 유저가 이 쿠폰 정책으로 쿠폰을 발급받았는지 확인 (중복 발급 방지)
            boolean alreadyUsed = couponRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicy.getId());
            if (alreadyUsed) {
                throw new ApiException(ALREADY_ISSUED_COUPON);
            }

            // 6. 쿠폰 생성 및 저장
            Coupon coupon = Coupon.of(userId, couponPolicyId, CouponStatus.ISSUED);
            couponRepository.save(coupon);

            // 7. 쿠폰 발급 수량 1 증가
            couponPolicy.increaseIssuedCount();
            couponPolicyRepository.save(couponPolicy);
        }
    }
}
