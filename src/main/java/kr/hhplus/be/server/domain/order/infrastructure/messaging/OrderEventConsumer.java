package kr.hhplus.be.server.domain.order.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.dataplatform.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.order.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final DataPlatformClient dataPlatformClient;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "data-platform")
    public void consume(String message) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            dataPlatformClient.sendOrderData(event);
        } catch (Exception e) {
            log.error("주문 데이터 전송 실패, message={}", message, e);
        }
    }
}
