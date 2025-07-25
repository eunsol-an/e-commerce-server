package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    Coupon toDomain(CouponJpaEntity entity);
    CouponJpaEntity toEntity(Coupon domain);
}
