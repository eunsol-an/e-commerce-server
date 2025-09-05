package kr.hhplus.be.server.domain.coupon.application.listener;

import kr.hhplus.be.server.domain.coupon.domain.event.CouponRequestedEvent;
import kr.hhplus.be.server.domain.coupon.infrastructure.messaging.CouponEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestedListener {
    private final CouponEventProducer couponEventProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponRequestedEvent event) {
        try {
            couponEventProducer.publish(event);
        } catch (Exception e) {
            log.error("쿠폰 데이터 전송 실패, couponPolicyId={}, userId={}", event.couponPolicyId(), event.userId(), e);
        }
    }
}
