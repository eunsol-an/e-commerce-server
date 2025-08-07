package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
    public Coupon toDomain(CouponJpaEntity entity) {
        if (entity == null) return null;
        return Coupon.of(entity.getId(), entity.getUserId(), entity.getCouponPolicyId(), entity.getStatus(), entity.getVersion());
    }

    public CouponJpaEntity toEntity(Coupon domain) {
        if (domain == null) return null;
        return new CouponJpaEntity(domain.getId(), domain.getUserId(), domain.getCouponPolicyId(), domain.getStatus(), domain.getVersion());
    }
}
