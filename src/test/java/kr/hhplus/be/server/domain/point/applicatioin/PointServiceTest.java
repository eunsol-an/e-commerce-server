package kr.hhplus.be.server.domain.point.applicatioin;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("PointService 테스트")
@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    PointRepository pointRepository;

    @InjectMocks
    PointService pointService;

    @Nested
    @DisplayName("포인트 충전 시")
    class ChargePoint {
        @Test
        @DisplayName("정상적으로 포인트가 충전된다")
        void 포인트충전() {
            // given
            Long userId = 1L;
            long originalPoint = 0L;
            long amount = 1000L;

            // 기존 유저 포인트 객체
            UserPoint existing = PointSteps.포인트생성(userId, originalPoint); // 포인트 0부터 시작
            UserPoint expected = PointSteps.포인트생성(userId, originalPoint + amount); // 포인트 충전된 상태

            given(pointRepository.findById(userId)).willReturn(Optional.of(existing));
            given(pointRepository.save(any(UserPoint.class))).willReturn(expected);

            // when
            pointService.charge(PointSteps.포인트충전_커맨드(userId, amount));

            // then
            then(pointRepository).should(times(1)).findById(userId);
            then(pointRepository).should(times(1)).save(any(UserPoint.class));

            ArgumentCaptor<UserPoint> captor = ArgumentCaptor.forClass(UserPoint.class);
            then(pointRepository).should().save(captor.capture());

            UserPoint saved = captor.getValue();
            assertEquals(userId, saved.getId());
            assertEquals(originalPoint + amount, saved.getBalance());
        }

        @Test
        @DisplayName("최대 잔고 금액 초과 충전시 예외가 발생한다")
        void 포인트충전_최대잔고초과_예외() {
            Long userId = 1L;
            given(pointRepository.findById(userId)).willReturn(Optional.of(PointSteps.포인트생성(userId, 100000L)));

            assertThrows(ApiException.class, () -> {
                pointService.charge(PointSteps.포인트충전_커맨드(userId, 1L));
            });
        }
    }

    @Nested
    @DisplayName("포인트 사용 시")
    class UsePoint {
        @Test
        @DisplayName("정상적으로 포인트가 차감된다")
        void 포인트사용() {
            Long userId = 1L;
            long originalPoint = 1000L;
            long amount = 500L;

            // 기존 유저 포인트 객체
            UserPoint existing = PointSteps.포인트생성(userId, originalPoint); // 포인트 1000부터 시작
            UserPoint expected = PointSteps.포인트생성(userId, originalPoint - amount); // 포인트 사용된 상태

            given(pointRepository.findById(userId)).willReturn(Optional.of(existing));
            given(pointRepository.save(any(UserPoint.class))).willReturn(expected);

            pointService.use(PointSteps.포인트사용_커맨드(userId, amount));

            then(pointRepository).should(times(1)).findById(userId);
            then(pointRepository).should(times(1)).save(any(UserPoint.class));

            ArgumentCaptor<UserPoint> captor = ArgumentCaptor.forClass(UserPoint.class);
            then(pointRepository).should().save(captor.capture());

            UserPoint saved = captor.getValue();
            assertEquals(userId, saved.getId());
            assertEquals(originalPoint - amount, saved.getBalance());
        }

        @Test
        @DisplayName("잔액 부족시 예외가 발생한다")
        void 포인트사용_잔고부족_예외() {
            Long userId = 1L;
            given(pointRepository.findById(userId)).willReturn(Optional.of(PointSteps.포인트생성(userId, 500L)));

            assertThrows(ApiException.class, () -> {
                pointService.use(PointSteps.포인트사용_커맨드(userId, 1000L));
            });
        }
    }

    @Nested
    @DisplayName("포인트 조회 시")
    class GetPoint {
        @Test
        @DisplayName("포인트가 정상적으로 조회된다")
        void 포인트조회() {
            Long userId = 1L;
            long amount = 1000L;

            UserPoint existing = PointSteps.포인트생성(userId, amount); // 포인트 충전된 상태
            given(pointRepository.findById(userId)).willReturn(Optional.of(existing));

            PointInfo.Balance balance = pointService.getBalance(userId);

            then(pointRepository).should(times(1)).findById(userId);
            assertEquals(userId, balance.userId());
            assertEquals(amount, balance.amount());
        }

        @Test
        @DisplayName("존재하지 않는 유저 ID로 조회 시 예외가 발생한다")
        void 포인트조회_예외() {
            Long userId = 1L;
            given(pointRepository.findById(userId)).willReturn(Optional.empty());

            assertThrows(ApiException.class, () -> pointService.getBalance(userId));
        }
    }
}