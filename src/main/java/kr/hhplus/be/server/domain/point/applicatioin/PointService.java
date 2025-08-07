package kr.hhplus.be.server.domain.point.applicatioin;


import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
import kr.hhplus.be.server.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.exception.ErrorCode.POINT_NOT_ENOUGH;
import static kr.hhplus.be.server.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    @Transactional
    @Retryable(
            value = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public void charge(PointCommand.Charge command) {
        UserPoint userPoint = pointRepository.findByIdWithOptimisticLock(command.userId())
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        userPoint.charge(command.amount());

        pointRepository.save(userPoint);
    }

    @Transactional
    @Retryable(
            value = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public void use(PointCommand.Use command) {
        UserPoint userPoint = pointRepository.findByIdWithOptimisticLock(command.userId())
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        userPoint.use(command.amount());

        pointRepository.save(userPoint);
    }

    @Transactional(readOnly = true)
    public PointInfo.Balance getBalance(Long userId) {
        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        return PointInfo.Balance.of(userPoint.getId(), userPoint.getBalance());
    }

    public UserPoint validateBalance(Long userId, long amount) {
        UserPoint userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND));
        if (userPoint.isInsufficientBalance(amount)) {
            throw new ApiException(POINT_NOT_ENOUGH);
        }

        return userPoint;
    }
}
