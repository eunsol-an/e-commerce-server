package kr.hhplus.be.server.domain.point.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;
import kr.hhplus.be.server.domain.point.infrastructure.persistence.entity.UserPointJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointMapper {
    UserPoint toDomain(UserPointJpaEntity entity);
    UserPointJpaEntity toEntity(UserPoint domain);
}
