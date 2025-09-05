package kr.hhplus.be.server.domain.coupon.infrastructure.persistence;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponPolicyJpaEntity;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.mapper.CouponPolicyMapper;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository.CouponPolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponPolicyRepositoryImpl implements CouponPolicyRepository {
    private final CouponPolicyJpaRepository couponPolicyJpaRepository;
    private final CouponPolicyMapper couponPolicyMapper;

    @Override
    public Optional<CouponPolicy> findById(Long couponPolicyId) {
        return couponPolicyJpaRepository.findById(couponPolicyId)
                .map(couponPolicyMapper::toDomain);
    }

    @Override
    public Optional<CouponPolicy> findByIdWithPessimisticLock(Long couponPolicyId) {
        return couponPolicyJpaRepository.findByIdWithPessimisticLock(couponPolicyId)
                .map(couponPolicyMapper::toDomain);
    }

    @Override
    public CouponPolicy save(CouponPolicy couponPolicy) {
        CouponPolicyJpaEntity couponPolicyJpaEntity = couponPolicyMapper.toEntity(couponPolicy);
        return couponPolicyMapper.toDomain(couponPolicyJpaRepository.save(couponPolicyJpaEntity));
    }

    @Override
    public int tryIncreaseIssuedCount(Long couponPolicyId) {
        return couponPolicyJpaRepository.increaseIssuedCountIfAvailable(couponPolicyId);
    }

    @Override
    public void deleteAllInBatch() {
        couponPolicyJpaRepository.deleteAllInBatch();
    }
}
