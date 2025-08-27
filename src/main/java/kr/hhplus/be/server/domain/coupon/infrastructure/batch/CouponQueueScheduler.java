package kr.hhplus.be.server.domain.coupon.infrastructure.batch;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponMemoryRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponQueueScheduler {
    private final CouponRepository couponRepository;
    private final CouponMemoryRepository couponMemoryRepository;

    @Scheduled(fixedDelay = 5000) // 5초마다 처리
    public void processCouponQueue() {
        Long couponPolicyId = 1L; // 예시

        List<Long> queue = couponMemoryRepository.dequeue(couponPolicyId, 50);
        if (queue == null || queue.isEmpty()) return;

        for (Long userId : queue) {
            // DB 저장
            Coupon coupon = Coupon.create(userId, couponPolicyId, CouponStatus.ISSUED);
            couponRepository.save(coupon);
        }
    }
}
