package kr.hhplus.be.server.domain.coupon.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Coupon {
    private Long id;
    private Long userId;
    private Long couponPolicyId;
    private CouponStatus status;

    @Builder
    private Coupon(Long id, Long userId, Long couponPolicyId, CouponStatus status) {
        this.id = id;
        this.userId = userId;
        this.couponPolicyId = couponPolicyId;
        this.status = status;
    }

    public static Coupon of(Long id, Long userId, Long couponPolicyId, CouponStatus status) {
        return Coupon.builder()
                .id(id)
                .userId(userId)
                .couponPolicyId(couponPolicyId)
                .status(status)
                .build();
    }

    public static Coupon create(Long userId, Long couponPolicyId, CouponStatus status) {
        return Coupon.builder()
                .userId(userId)
                .couponPolicyId(couponPolicyId)
                .status(status)
                .build();
    }

    public boolean isAvailable() {
        return this.status == CouponStatus.ISSUED;
    }

    public void use() {
        this.status = CouponStatus.USED;
    }
}
