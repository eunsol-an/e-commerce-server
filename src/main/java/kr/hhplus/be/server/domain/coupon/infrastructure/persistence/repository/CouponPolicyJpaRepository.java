package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.repository;

import kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity.CouponPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyJpaEntity, Long> {
}
