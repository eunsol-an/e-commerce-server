package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.domain.point.applicatioin.PointService;
import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
import kr.hhplus.be.server.domain.product.application.ProductService;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("OrderFacade 통합 테스트")
class OrderFacadeIntegrationTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CouponPolicyRepository couponPolicyRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private PointRepository pointRepository;
    @Autowired private CouponService couponService;

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // 상품 생성
        Product product = productRepository.save(Product.create("머쉬룸 스탠드", 5000L, 10));
        productId = product.getId();

        // 유저에게 포인트 제공
        UserPoint userPoint = pointRepository.save(UserPoint.create(20000L));
        userId = userPoint.getId();
    }

    @Test
    @DisplayName("쿠폰 없이 정상 주문 및 결제가 처리된다")
    void 정상_주문_결제_쿠폰없음() {
        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, 2)), // 5000 * 2
                null
        );

        orderFacade.orderPayment(command);

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getItems()).hasSize(1);
        assertThat(orders.get(0).getPaidAmount()).isEqualTo(20000L);
    }

    @Test
    @DisplayName("쿠폰을 사용한 주문 결제가 정상 처리된다")
    void 쿠폰_사용_정상_주문() {
        // given
        long discountAmount = 3000L;
        CouponPolicy policy = CouponPolicy.of(null, discountAmount, 100, 0, 365);
        policy = couponPolicyRepository.save(policy);

        // 쿠폰 발급
        couponService.issue(userId, policy.getId());
        Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(policy.getId(), userId)
                .orElseThrow();

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, 2)), // 5000 * 2
                coupon.getId()
        );

        // when
        orderFacade.orderPayment(command);

        // then
        Order order = orderRepository.findAll().get(0);
        assertThat(order.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(order.getPaidAmount()).isEqualTo(17000L);
    }

    @Test
    @DisplayName("포인트 부족으로 주문 결제 실패")
    void 포인트_부족_실패() {
        // 유저 포인트 초기화 (부족하게 설정)
        pointRepository.save(UserPoint.of(userId, 100L));

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, 2)), // 5000 * 2
                null
        );

        ApiException ex = assertThrows(ApiException.class, () -> orderFacade.orderPayment(command));
        assertThat(ex.getErrorCode()).isEqualTo(POINT_NOT_ENOUGH);
    }

    @Test
    @DisplayName("재고 부족으로 주문 결제 실패")
    void 재고_부족_실패() {
        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, 1000)), // 과다 수량
                null
        );

        ApiException ex = assertThrows(ApiException.class, () -> orderFacade.orderPayment(command));
        assertThat(ex.getErrorCode()).isEqualTo(OUT_OF_STOCK);
    }

    @Test
    @DisplayName("발급 쿠폰이 존재하지 않아 결제 실패")
    void 쿠폰_없음_실패() {
        Long invalidCouponId = 999L;

        OrderCommand.Create command = new OrderCommand.Create(
                userId,
                List.of(new OrderCommand.Item(productId, 1)),
                invalidCouponId
        );

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderFacade.orderPayment(command);
        });
        AssertionsForClassTypes.assertThat(exception.getErrorCode()).isEqualTo(COUPON_NOT_FOUNT);
    }
}
