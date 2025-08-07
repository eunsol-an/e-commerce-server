package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("CouponService 동시성 테스트")
public class CouponServiceConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(CouponServiceConcurrencyTest.class);

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    private CouponPolicy couponPolicy;
    private final int INITIAL_QUANTITY = 100;

    @BeforeEach
    void setUp() {
        couponPolicy = couponPolicyRepository.save(
                CouponPolicy.of(null, 1000, INITIAL_QUANTITY, 0, 30)
        );
    }

    @AfterEach
    void tearDown() {
        couponRepository.deleteAllInBatch();
        couponPolicyRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시에 100개의 요청으로 쿠폰이 정상 발급된다")
    void 쿠폰발급_동시요청_100개() throws InterruptedException {
        // given
        final int threadCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            int userId = i;
            executorService.submit(() -> {
                try {
                    couponService.issue((long) (userId + 1), couponPolicy.getId());
                    successCount.incrementAndGet(); // 성공한 경우
                } catch (Exception e) {
                    failCount.incrementAndGet(); // 실패한 경우
                    log.info("요청 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        final CouponPolicy result = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();

        // then
        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failCount.get());
        log.info("최종 쿠폰 발급 개수: {}", result.getIssuedCount());
        assertThat(result.getIssuedCount()).isEqualTo(INITIAL_QUANTITY);
    }

    @Test
    @DisplayName("10개의 동시 요청 중 1개의 쿠폰만 정상 사용된다")
    void 쿠폰사용_동시요청_10개() throws InterruptedException {
        // given

        // 쿠폰 발급
        Long userId = 1L;
        couponService.issue(userId, couponPolicy.getId());
        Coupon coupon = couponRepository.findByCouponPolicyIdAndUserId(couponPolicy.getId(), userId).orElseThrow();

        final int threadCount = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    couponService.applyCoupon(coupon.getId(), userId);
                    successCount.incrementAndGet(); // 성공한 경우
                } catch (Exception e) {
                    failCount.incrementAndGet(); // 실패한 경우
                    log.info("요청 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        final Coupon result = couponRepository.findByCouponPolicyIdAndUserId(couponPolicy.getId(), userId).orElseThrow();

        // then
        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failCount.get());
        log.info("최종 쿠폰 상태: {}", result.getStatus());
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(CouponStatus.USED);
    }
}
