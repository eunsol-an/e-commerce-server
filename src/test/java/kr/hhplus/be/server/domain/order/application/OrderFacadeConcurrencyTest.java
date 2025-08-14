package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.coupon.application.CouponService;
import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("OrderFacade 동시성 테스트")
class OrderFacadeConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(OrderFacadeConcurrencyTest.class);

    @Autowired private OrderFacade orderFacade;
    @Autowired private ProductRepository productRepository;
    @Autowired private CouponPolicyRepository couponPolicyRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private PointRepository pointRepository;
    @Autowired private CouponService couponService;

    private Long productAId;
    private Long productBId;
    private Long couponPolicyId;

    private final long DISCOUNT_AMOUNT = 3000L;
    private final int INITIAL_QUANTITY_PRODUCT_A = 200;
    private final int INITIAL_QUANTITY_PRODUCT_B = 100;

    @BeforeEach
    void setUp() {
        // 상품 생성
        Product productA = productRepository.save(Product.create("머쉬룸 스탠드", 5000L, INITIAL_QUANTITY_PRODUCT_A));
        Product productB = productRepository.save(Product.create("푹신한 장모 러그", 10000L, INITIAL_QUANTITY_PRODUCT_B));
        productAId = productA.getId();
        productBId = productB.getId();

        // 쿠폰 정책 생성
        CouponPolicy couponPolicy = couponPolicyRepository.save(CouponPolicy.of(null, DISCOUNT_AMOUNT, 1000, 0, 365));
        couponPolicyId = couponPolicy.getId();
    }

    @Test
    @DisplayName("동시에 100개의 요청으로 주문을 처리한다")
    void 주문_동시요청_100개() throws InterruptedException {
        // given
        final int threadCount = 100;
        final int quantityA = 2;
        final int quantityB = 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 유저에게 포인트 제공
                    UserPoint userPoint = pointRepository.save(UserPoint.create(20000L));
                    Long userId = userPoint.getId();

                    // 쿠폰 발급
                    couponService.issue(userId, couponPolicyId);
                    Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(couponPolicyId, userId)
                            .orElseThrow();

                    OrderCommand.Create command = new OrderCommand.Create(
                            userId,
                            List.of(
                                    new OrderCommand.Item(productAId, quantityA),
                                    new OrderCommand.Item(productBId, quantityB)),
                            coupon.getId()
                    );

                    orderFacade.orderPayment(command);

                    successCount.incrementAndGet(); // 성공한 경우
                } catch (Exception e) {
                    failCount.incrementAndGet(); // 실패한 경우
                    e.printStackTrace();
                    log.info("요청 실패: {}", ((ApiException) e).getErrorCode().getMessage());
                    log.info("요청 실패: {}", e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        final Product resultA = productRepository.findById(productAId).orElseThrow();
        final Product resultB = productRepository.findById(productBId).orElseThrow();

        // then
        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failCount.get());
        log.info("최종 상품A 재고 개수: {}", resultA.getStockQuantity());
        log.info("최종 상품B 재고 개수: {}", resultB.getStockQuantity());
        assertThat(resultA.getStockQuantity()).isEqualTo(INITIAL_QUANTITY_PRODUCT_A - (threadCount * quantityA));
        assertThat(resultB.getStockQuantity()).isEqualTo(INITIAL_QUANTITY_PRODUCT_B - (threadCount * quantityB));
    }
}