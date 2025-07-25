package kr.hhplus.be.server.domain.coupon.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_policy")
@Getter
@NoArgsConstructor
public class CouponPolicyJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long discountAmount;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int issuedCount;

    @Column(nullable = false)
    private int validDays;
}
