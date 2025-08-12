package kr.hhplus.be.server.domain.product.application;

import kr.hhplus.be.server.domain.order.application.OrderCommand;
import kr.hhplus.be.server.domain.product.domain.model.Product;
import kr.hhplus.be.server.domain.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("ProductService 동시성 테스트")
public class ProductServiceConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceConcurrencyTest.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;
    private final int INITIAL_QUANTITY = 100;

    @BeforeEach
    void setUp() {
        // 상품 생성
        Product product = productRepository.save(Product.create("머쉬룸 스탠드", 5000L, INITIAL_QUANTITY));
        productId = product.getId();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("재고를 1개 감소시킨다")
    void 재고감소() {
        productService.deductStock(List.of(OrderCommand.Item.of(productId, 1)));

        Optional<Product> product = productRepository.findById(productId);

        assertEquals(99, product.get().getStockQuantity());
    }

    @Test
    @DisplayName("동시에 100개의 요청으로 재고를 감소시킨다")
    void 재고감소_동시요청_100개() throws InterruptedException {
        // given
        final int threadCount = 100;
        final int quantity = 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.deductStock(List.of(OrderCommand.Item.of(productId, quantity)));
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
        final Product result = productRepository.findById(productId).orElseThrow();

        // then
        log.info("성공한 요청 수: {}", successCount.get());
        log.info("실패한 요청 수: {}", failCount.get());
        log.info("최종 재고 개수: {}", result.getStockQuantity());
        assertThat(result.getStockQuantity()).isEqualTo(INITIAL_QUANTITY - (threadCount * quantity));
    }
}
