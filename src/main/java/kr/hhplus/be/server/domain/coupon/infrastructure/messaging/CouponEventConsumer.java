package kr.hhplus.be.server.domain.coupon.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.coupon.domain.event.CouponRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponEventConsumer {

    private final CouponService couponService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "coupon-events", groupId = "coupon-service-group")
    public void consume(String message) {
        try {
            CouponRequestedEvent event = objectMapper.readValue(message, CouponRequestedEvent.class);
            Long userId = event.userId();
            Long couponPolicyId = event.couponPolicyId();

            // 중복 발급 + 수량 체크
            boolean granted = couponService.grantCouponIfNotExists(userId, couponPolicyId);
            if(granted) {
                log.info("쿠폰 발급 성공: userId={}, couponId={}", userId, couponPolicyId);
            } else {
                log.info("쿠폰 발급 실패: userId={}, couponId={}", userId, couponPolicyId);
            }

        } catch (Exception e) {
            log.error("쿠폰 이벤트 처리 실패, message={}", message, e);
        }
    }
}