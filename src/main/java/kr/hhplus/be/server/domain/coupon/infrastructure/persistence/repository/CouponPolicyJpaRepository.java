package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM CouponPolicyJpaEntity AS p WHERE p.id = :id")
    Optional<CouponPolicyJpaEntity> findByIdWithPessimisticLock(Long id);
}
