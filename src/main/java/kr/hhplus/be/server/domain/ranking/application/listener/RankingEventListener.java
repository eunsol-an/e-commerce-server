package kr.hhplus.be.server.domain.ranking.application.listener;

import kr.hhplus.be.server.domain.order.domain.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.ranking.application.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingEventListener {
    private final RankingService rankingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedEvent event) {
        log.info("주문 생성 이벤트 수신");
        for (OrderItem item : event.items()) {
            try {
                rankingService.increaseScore(item.getProductId(), item.getQuantity());
            } catch (Exception ex) {
                log.error("상품 점수 증가 실패, orderId={}, productId={}, quantity={}",
                        event.orderId(), item.getProductId(), item.getQuantity(), ex);
            }
        }
    }
}

