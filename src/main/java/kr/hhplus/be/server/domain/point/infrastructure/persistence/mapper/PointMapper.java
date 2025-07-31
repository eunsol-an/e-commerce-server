package kr.hhplus.be.server.domain.point.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.infrastructure.persistence.entity.UserPointJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PointMapper {
    public UserPoint toDomain(UserPointJpaEntity entity) {
        if (entity == null) return null;
        return UserPoint.of(entity.getId(), entity.getBalance());
    }

    public UserPointJpaEntity toEntity(UserPoint domain) {
        if (domain == null) return null;
        return new UserPointJpaEntity(domain.getId(), domain.getBalance());
    }
}
