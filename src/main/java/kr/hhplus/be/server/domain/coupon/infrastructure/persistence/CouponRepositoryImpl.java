package kr.hhplus.be.server.domain.coupon.infrastructure.persistence;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.mapper.CouponMapper;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;
    private final CouponMapper couponMapper;

    @Override
    public Optional<Coupon> findByCouponPolicyIdAndUserId(Long couponPolicyId, Long userId) {
        return couponJpaRepository.findByIdAndUserId(couponPolicyId, userId)
                .map(couponMapper::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity couponJpaEntity = couponMapper.toEntity(coupon);
        return couponMapper.toDomain(couponJpaRepository.save(couponJpaEntity));
    }
}
