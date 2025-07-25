package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.point.applicatioin.PointService;
import kr.hhplus.be.server.domain.product.application.ProductService;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("OrderFacade 테스트")
@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;
    @Mock
    private PointService pointService;
    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderFacade orderFacade;

    @Test
    @DisplayName("정상 주문 생성 및 결제")
    void 주문_결제_정상() {
        // given
        Long userId = 1L;
        Long couponPolicyId = 10L;
        Long productId = 100L;
        int quantity = 2;
        long price = 5000L;
        long totalPrice = price * quantity;
        long discount = 3000L;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, quantity)),
                couponPolicyId
        );

        Product product = new Product(productId, "머쉬룸 스탠드", price, 10);
        Coupon coupon = Coupon.of(99L, couponPolicyId, CouponStatus.ISSUED);
        CouponPolicy couponPolicy = CouponPolicy.of(
                couponPolicyId,
                discount,
                quantity,
                100,
                365);

        when(productService.validateStock(productId, quantity)).thenReturn(product);
        when(couponService.validate(couponPolicyId, userId)).thenReturn(coupon);
        when(couponService.getCouponPolicy(coupon.getId())).thenReturn(couponPolicy);
        when(productService.calculateTotalItemPrice(command.items())).thenReturn(totalPrice);

        // when
        orderFacade.orderPayment(command);

        // then
        verify(productService, times(1)).validateStock(productId, quantity);
        verify(couponService, times(1)).validate(couponPolicyId, userId);
        verify(couponService, times(1)).getCouponPolicy(coupon.getId());
        verify(couponService, times(1)).use(couponPolicyId, userId);
        verify(pointService, times(1)).validateBalance(userId, totalPrice - discount);
        verify(pointService, times(1)).use(any());
        verify(productService, times(1)).deductStock(command.items());
        verify(orderService, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void 재고_부족_예외() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 100;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, quantity)),
                null
        );

        when(productService.validateStock(productId, quantity)).thenThrow(new RuntimeException("재고 부족"));

        // when & then
        RuntimeException e = assertThrows(RuntimeException.class, () -> orderFacade.orderPayment(command));
        assertEquals("재고 부족", e.getMessage());
    }

    @Test
    @DisplayName("쿠폰이 유효하지 않으면 예외 발생")
    void 쿠폰_유효성_검증_실패() {
        // given
        Long userId = 1L;
        Long couponPolicyId = 99L;
        Long productId = 1L;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, 1)),
                couponPolicyId
        );

        Product product = new Product(productId, "머쉬룸 스탠드", 1000L, 5);
        when(productService.validateStock(any(), anyInt())).thenReturn(product);
        when(couponService.validate(couponPolicyId, userId)).thenThrow(new RuntimeException("쿠폰 만료"));

        // when & then
        RuntimeException e = assertThrows(RuntimeException.class, () -> orderFacade.orderPayment(command));
        assertEquals("쿠폰 만료", e.getMessage());
    }

    @Test
    @DisplayName("포인트 부족 시 예외 발생")
    void 포인트_부족_예외() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 1;
        long price = 10000L;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, quantity)),
                null
        );

        Product product = new Product(productId, "머쉬룸 스탠드", price, 10);

        when(productService.validateStock(productId, quantity)).thenReturn(product);
        when(productService.calculateTotalItemPrice(command.items())).thenReturn(price);
        doThrow(new RuntimeException("포인트 부족")).when(pointService).validateBalance(userId, price);

        // when & then
        RuntimeException e = assertThrows(RuntimeException.class, () -> orderFacade.orderPayment(command));
        assertEquals("포인트 부족", e.getMessage());
    }

    @Test
    @DisplayName("쿠폰 없이도 정상 주문 가능")
    void 쿠폰_없이_정상_주문() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 1;
        long price = 3000L;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, quantity)),
                null
        );

        Product product = new Product(productId, "머쉬룸 스탠드", price, 10);
        when(productService.validateStock(productId, quantity)).thenReturn(product);
        when(productService.calculateTotalItemPrice(command.items())).thenReturn(price);

        // when
        orderFacade.orderPayment(command);

        // then
        verify(couponService, never()).validate(any(), any());
        verify(pointService, times(1)).validateBalance(userId, price);
        verify(pointService, times(1)).use(any());
        verify(productService, times(1)).deductStock(command.items());
        verify(orderService, times(1)).save(any(Order.class));
    }
}
