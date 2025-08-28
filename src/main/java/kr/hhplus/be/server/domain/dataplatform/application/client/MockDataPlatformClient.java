package kr.hhplus.be.server.domain.dataplatform.application.client;

import kr.hhplus.be.server.domain.order.domain.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MockDataPlatformClient implements DataPlatformClient{

    @Override
    public void sendOrderData(OrderCreatedEvent event) {
        // 실제 API 호출 대신, 로그 작성
        log.info("주문 데이터 전송됨: orderId={}, userId={}", event.orderId(), event.userId());
    }
}
