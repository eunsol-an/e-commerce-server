package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.model.OrderItem;
import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("OrderService 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문이 정상적으로 저장된다")
    void 주문저장() {
        // given
        Order newOrder = Order.create(
                1L,
                10L,
                List.of(
                        OrderItem.create(101L, 2, 2000),
                        OrderItem.create(102L, 1, 3000)
                ),
                7000,
                1000
        );

        Order savedOrder = newOrder; // 테스트에서는 저장 후 객체가 동일하다고 가정

        when(orderRepository.save(newOrder)).thenReturn(savedOrder);

        // stub OrderInfo.Order.of()
        OrderInfo.Order expectedInfo = mock(OrderInfo.Order.class);
        try (var mockStatic = mockStatic(OrderInfo.Order.class)) {
            mockStatic.when(() -> OrderInfo.Order.of(savedOrder)).thenReturn(expectedInfo);

            // when
            OrderInfo.Order result = orderService.save(newOrder);

            // then
            assertThat(result).isEqualTo(expectedInfo);
            verify(orderRepository, times(1)).save(newOrder);
        }
    }
}