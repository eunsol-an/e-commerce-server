package kr.hhplus.be.server.domain.dataplatform.application.listener;

import kr.hhplus.be.server.domain.dataplatform.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.order.domain.event.OrderCreatedEvent;
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
    private final DataPlatformClient dataPlatformClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedEvent event) {
        try {
            dataPlatformClient.sendOrderData(event);
        } catch (Exception ex) {
            log.error("주문 데이터 전송 실패, orderId={}", event.orderId(), ex);
        }
    }
}

