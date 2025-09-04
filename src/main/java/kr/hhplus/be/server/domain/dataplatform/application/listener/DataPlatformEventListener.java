package kr.hhplus.be.server.domain.dataplatform.application.listener;

import kr.hhplus.be.server.domain.order.domain.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.infrastructure.messaging.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformEventListener {
    private final OrderEventProducer orderEventProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedEvent event) {
        try {
            orderEventProducer.publish(event);
        } catch (Exception e) {
            log.error("주문 데이터 전송 실패, orderId={}", event.orderId(), e);
        }
    }
}

