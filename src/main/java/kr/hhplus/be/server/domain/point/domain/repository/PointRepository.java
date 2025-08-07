package kr.hhplus.be.server.domain.point.domain.repository;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;

import java.util.Optional;

public interface PointRepository {
    Optional<UserPoint> findById(Long id);
    Optional<UserPoint> findByIdWithOptimisticLock(Long id);
    UserPoint save(UserPoint userPoint);
    void deleteAllInBatch();
}
