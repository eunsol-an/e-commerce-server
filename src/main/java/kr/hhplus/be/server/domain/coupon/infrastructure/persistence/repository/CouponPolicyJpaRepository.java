package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM CouponPolicyJpaEntity AS p WHERE p.id = :id")
    Optional<CouponPolicyJpaEntity> findByIdWithPessimisticLock(Long id);

    @Modifying
    @Query("UPDATE CouponPolicyJpaEntity c SET c.issuedCount = c.issuedCount + 1 " +
            "WHERE c.id = :couponPolicyId AND c.issuedCount < c.totalQuantity")
    int increaseIssuedCountIfAvailable(@Param("couponPolicyId") Long couponPolicyId);
}
