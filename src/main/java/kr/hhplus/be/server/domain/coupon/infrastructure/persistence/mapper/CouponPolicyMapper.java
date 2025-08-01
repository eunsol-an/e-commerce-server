package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.mapper;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponPolicyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponPolicyMapper {
    public CouponPolicy toDomain(CouponPolicyJpaEntity entity) {
        if (entity == null) return null;
        return CouponPolicy.of(
                entity.getId(),
                entity.getDiscountAmount(),
                entity.getTotalQuantity(),
                entity.getIssuedCount(),
                entity.getValidDays());
    }
    public CouponPolicyJpaEntity toEntity(CouponPolicy domain) {
        if (domain == null) return null;
        return new CouponPolicyJpaEntity(
                domain.getId(),
                domain.getDiscountAmount(),
                domain.getTotalQuantity(),
                domain.getIssuedCount(),
                domain.getValidDays());
    }
}
