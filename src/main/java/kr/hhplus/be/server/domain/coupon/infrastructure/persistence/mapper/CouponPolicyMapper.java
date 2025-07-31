package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponPolicyJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponPolicyMapper {
    CouponPolicy toDomain(CouponPolicyJpaEntity entity);
    CouponPolicyJpaEntity toEntity(CouponPolicy domain);
}
