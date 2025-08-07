package kr.hhplus.be.server.domain.point.applicatioin;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class PointSteps {
    public static UserPoint 포인트생성(Long userId, long amount) {
        return UserPoint.of(userId, amount, 0L);
    }

    public static PointCommand.Charge 포인트충전_커맨드(Long userId, long amount) {
        return PointCommand.Charge.of(UserPoint.of(userId, amount, 0L));
    }

    public static PointCommand.Use 포인트사용_커맨드(Long userId, long amount) {
        return PointCommand.Use.of(UserPoint.of(userId, amount, 0L));
    }

    public static void 기존_포인트_조회됨(PointRepository pointRepository, UserPoint existing) {
        given(pointRepository.findById(existing.getId())).willReturn(Optional.of(existing));
    }

    public static void 포인트_저장됨(PointRepository pointRepository, UserPoint expected) {
        given(pointRepository.save(any(UserPoint.class))).willReturn(expected);
    }
}
