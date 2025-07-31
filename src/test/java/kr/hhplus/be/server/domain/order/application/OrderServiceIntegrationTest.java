package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("OrderService 통합 테스트")
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Order newOrder;

    @BeforeEach
    void setUp() {
        newOrder = Order.create(
                1L,
                10L,
                List.of(
                        OrderItem.create(101L, 2, 2000),
                        OrderItem.create(102L, 1, 3000)
                )
        );
        newOrder.applyDiscount(1000L);
    }

    @Test
    @DisplayName("주문이 DB에 정상적으로 저장된다")
    void 주문_저장_성공() {
        // when
        OrderInfo.Order saved = orderService.save(newOrder);

        // then
        Order loaded = orderRepository.findById(saved.orderId())
                .orElseThrow(() -> new IllegalStateException("주문이 저장되지 않았습니다."));

        assertThat(loaded.getUserId()).isEqualTo(newOrder.getUserId());
        assertThat(loaded.getPaidAmount()).isEqualTo(newOrder.getPaidAmount());
        assertThat(loaded.getDiscountAmount()).isEqualTo(newOrder.getDiscountAmount());
        assertThat(loaded.getItems()).hasSize(2);
        assertThat(loaded.getItems().get(0).getProductId()).isEqualTo(101L);
    }
}
