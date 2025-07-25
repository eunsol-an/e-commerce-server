package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository;

import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {
    Optional<CouponJpaEntity> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndCouponPolicyId(Long userId, Long couponPolicyId);
}
