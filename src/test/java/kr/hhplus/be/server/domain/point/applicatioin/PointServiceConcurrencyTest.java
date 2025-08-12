package kr.hhplus.be.server.domain.point.applicatioin;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
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
@DisplayName("ProductService 동시성 테스트")
public class PointServiceConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(PointServiceConcurrencyTest.class);

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    private Long userId;
    private final long INITIAL_BALANCE = 1000L;

    @BeforeEach
    void setUp() {
        UserPoint saved = pointRepository.save(UserPoint.create(INITIAL_BALANCE));
        userId = saved.getId();
    }

    @AfterEach
    void tearDown() {
        pointRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시에 10개의 요청으로 잔액을 충전시킨다")
    void 포인트충전_동시요청_10개() throws InterruptedException {
        // given
        final int threadCount = 10;
        final long amount = 100L;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.charge(PointSteps.포인트충전_커맨드(userId, amount));
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
        final UserPoint result = pointRepository.findById(userId).orElseThrow();

        // then
        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failCount.get());
        log.info("최종 잔액: {}", result.getBalance());
        assertThat(result.getBalance()).isEqualTo(INITIAL_BALANCE + (threadCount * amount));
    }

    @Test
    @DisplayName("동시에 10개의 요청으로 잔액을 차감시킨다")
    void 포인트사용_동시요청_10개() throws InterruptedException {
        // given
        final int threadCount = 10;
        final long amount = 100L;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.use(PointSteps.포인트사용_커맨드(userId, amount));
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
        final UserPoint result = pointRepository.findById(userId).orElseThrow();

        // then
        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failCount.get());
        log.info("최종 잔액: {}", result.getBalance());
        assertThat(result.getBalance()).isEqualTo(INITIAL_BALANCE - (threadCount * amount));
    }


}
