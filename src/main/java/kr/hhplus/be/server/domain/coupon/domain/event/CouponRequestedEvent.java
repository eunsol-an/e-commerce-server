package kr.hhplus.be.server.domain.coupon.domain.event;

public record CouponRequestedEvent(
        Long couponPolicyId,
        Long userId) {

    public static CouponRequestedEvent of(Long couponPolicyId, Long userId) {
        return new CouponRequestedEvent(couponPolicyId, userId);
    }
}
