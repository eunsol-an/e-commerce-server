package kr.hhplus.be.server.domain.coupon.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.domain.event.CouponRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "coupon-events";

    public void publish(CouponRequestedEvent event) {
        Long userId = event.userId();
        Long couponPolicyId = event.couponPolicyId();
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(couponPolicyId), message);
            log.info("쿠폰 요청 이벤트 발행 완료: {}", message);
        } catch (JsonProcessingException e) {
            log.error("쿠폰 이벤트 직렬화 실패, userId={}, couponId={}", userId, couponPolicyId, e);
        }
    }
}