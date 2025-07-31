package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.point.applicatioin.PointService;
import kr.hhplus.be.server.domain.product.application.ProductService;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kr.hhplus.be.server.exception.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        int couponQuantity = 100;
        long price = 5000L;
        long totalPrice = price * quantity;
        long discount = 3000L;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, quantity)),
                couponPolicyId
        );

        Product product = new Product(productId, "머쉬룸 스탠드", price, 10);
        List<Product> products = List.of(product);
        Coupon coupon = Coupon.of(99L, couponPolicyId, CouponStatus.ISSUED);
        CouponPolicy couponPolicy = CouponPolicy.of(
                couponPolicyId,
                discount,
                couponQuantity,
                99,
                365);

        Order mockOrder = mock(Order.class);
        when(productService.validateStocks(command.items())).thenReturn(products);
        when(orderService.createOrder(command, products)).thenReturn(mockOrder);
        when(couponService.applyCoupon(couponPolicyId, userId)).thenReturn(discount);
        when(mockOrder.getPaidAmount()).thenReturn((totalPrice) - discount);

        // when
        orderFacade.orderPayment(command);

        // then
        verify(productService, times(1)).validateStocks(command.items());
        verify(orderService, times(1)).createOrder(command, products);
        verify(couponService, times(1)).applyCoupon(couponPolicyId, userId);
        verify(mockOrder, times(1)).applyDiscount(discount);
        verify(pointService, times(1)).validateBalance(userId, (price * quantity) - discount);
        verify(pointService, times(1)).use(any());
        verify(productService, times(1)).deductStock(command.items());
        verify(orderService, times(1)).save(mockOrder);
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

        when(productService.validateStocks(command.items())).thenThrow(new ApiException(OUT_OF_STOCK));

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> orderFacade.orderPayment(command));
        assertThat(exception.getErrorCode()).isEqualTo(OUT_OF_STOCK);
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
        List<Product> products = List.of(product);
        Order mockOrder = mock(Order.class);

        when(productService.validateStocks(command.items())).thenReturn(products);
        when(orderService.createOrder(command, products)).thenReturn(mockOrder);
        when(couponService.applyCoupon(couponPolicyId, userId)).thenThrow(new ApiException(COUPON_POLICY_NOT_FOUND));

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> orderFacade.orderPayment(command));
        assertThat(exception.getErrorCode()).isEqualTo(COUPON_POLICY_NOT_FOUND);
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
        List<Product> products = List.of(product);

        Order mockOrder = mock(Order.class);

        when(productService.validateStocks(command.items())).thenReturn(products);
        when(orderService.createOrder(command, products)).thenReturn(mockOrder);
        when(mockOrder.getPaidAmount()).thenReturn(price);
        when(pointService.validateBalance(userId, price)).thenThrow(new ApiException(POINT_NOT_ENOUGH));

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> orderFacade.orderPayment(command));
        assertThat(exception.getErrorCode()).isEqualTo(POINT_NOT_ENOUGH);
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
        List<Product> products = List.of(product);

        Order mockOrder = mock(Order.class);

        when(productService.validateStocks(command.items())).thenReturn(products);
        when(orderService.createOrder(command, products)).thenReturn(mockOrder);
        when(mockOrder.getPaidAmount()).thenReturn(price);

        // when
        orderFacade.orderPayment(command);

        // then
        verify(productService, times(1)).validateStocks(command.items());
        verify(orderService, times(1)).createOrder(command, products);
        verify(pointService, times(1)).validateBalance(userId, (price * quantity));
        verify(pointService, times(1)).use(any());
        verify(productService, times(1)).deductStock(command.items());
        verify(orderService, times(1)).save(mockOrder);
    }
}
