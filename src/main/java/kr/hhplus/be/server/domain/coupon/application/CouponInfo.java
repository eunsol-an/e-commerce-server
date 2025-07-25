package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponStatus;

public class CouponInfo {
    public record Coupon(
            Long id,
            Long userId,
            Long couponPolicyId,
            CouponStatus status
    ) {
        public static Coupon of(kr.hhplus.be.server.domain.coupon.domain.model.Coupon coupon) {
            return new Coupon(coupon.getId(), coupon.getUserId(), coupon.getCouponPolicyId(), coupon.getStatus());
        }
    }
}
