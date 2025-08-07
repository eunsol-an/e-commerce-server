package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT c FROM CouponJpaEntity c WHERE c.id = :id AND c.userId = :userId AND c.status = 'ISSUED'")
    Optional<CouponJpaEntity> findByIdAndUserIdAndStatusIssued(Long id, Long userId);
    Optional<CouponJpaEntity> findByCouponPolicyIdAndUserId(Long couponPolicyId, Long userId);
    boolean existsByUserIdAndCouponPolicyId(Long userId, Long couponPolicyId);
}
