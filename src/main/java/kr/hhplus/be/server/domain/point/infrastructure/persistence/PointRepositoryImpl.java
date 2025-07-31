package kr.hhplus.be.server.domain.point.infrastructure.persistence;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.domain.repository.PointRepository;
import kr.hhplus.be.server.domain.point.infrastructure.persistence.entity.UserPointJpaEntity;
import kr.hhplus.be.server.domain.point.infrastructure.persistence.mapper.PointMapper;
import kr.hhplus.be.server.domain.point.infrastructure.persistence.repository.PointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {
    private final PointJpaRepository pointJpaRepository;
    private final PointMapper pointMapper;

    @Override
    public Optional<UserPoint> findById(Long id) {
        return pointJpaRepository.findById(id)
                .map(pointMapper::toDomain);
    }

    @Override
    public UserPoint save(UserPoint userPoint) {
        UserPointJpaEntity pointJpaEntity = pointMapper.toEntity(userPoint);
        return pointMapper.toDomain(pointJpaRepository.save(pointJpaEntity));
    }
}
