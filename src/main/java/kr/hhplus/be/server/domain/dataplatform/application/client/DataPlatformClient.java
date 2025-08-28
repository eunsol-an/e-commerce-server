package kr.hhplus.be.server.domain.dataplatform.application.client;

import kr.hhplus.be.server.domain.order.domain.event.OrderCreatedEvent;

public interface DataPlatformClient {
    void sendOrderData(OrderCreatedEvent event);
}
