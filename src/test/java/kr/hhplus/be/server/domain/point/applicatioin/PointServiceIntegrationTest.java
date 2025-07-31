package kr.hhplus.be.server.domain.point.applicatioin;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("PointService 통합 테스트")
class PointServiceIntegrationTest {

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

    @Test
    @DisplayName("포인트 충전이 실제 DB에 반영된다")
    void 포인트충전_통합테스트() {
        // given
        long amount = 1000L;

        // when
        pointService.charge(PointSteps.포인트충전_커맨드(userId, amount));

        // then
        UserPoint result = pointRepository.findById(userId).orElseThrow();
        assertThat(result.getBalance()).isEqualTo(INITIAL_BALANCE + amount);
    }

    @Test
    @DisplayName("포인트 사용 후 잔고가 감소된다")
    void 포인트사용_통합테스트() {
        // given
        long amount = 600L;

        // when
        pointService.use(PointSteps.포인트사용_커맨드(userId, amount));

        // then
        UserPoint result = pointRepository.findById(userId).orElseThrow();
        assertThat(result.getBalance()).isEqualTo(INITIAL_BALANCE - amount);
    }

    @Test
    @DisplayName("잔액 부족 시 예외가 발생한다")
    void 포인트사용_잔액부족_예외_통합테스트() {
        // given
        long amount = 1001L;

        // when & then
        assertThatThrownBy(() -> pointService.use(PointSteps.포인트사용_커맨드(userId, amount)))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("충전 후 잔고 조회 시 금액이 정확하다")
    void 포인트조회_통합테스트() {
        // given
        long amount = 2000L;
        pointService.charge(PointSteps.포인트충전_커맨드(userId, amount));

        // when
        var result = pointService.getBalance(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.amount()).isEqualTo(INITIAL_BALANCE + amount);
    }
}
