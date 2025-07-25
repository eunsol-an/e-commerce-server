package kr.hhplus.be.server.domain.coupon.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coupon 도메인 테스트")
class CouponTest {

    @Test
    @DisplayName("정상적인 쿠폰 생성 시 필드 값이 설정된다")
    void 쿠폰_생성() {
        Long userId = 1L;
        Long couponPolicyId = 100L;

        Coupon coupon = Coupon.of(userId, couponPolicyId, CouponStatus.ISSUED);

        assertNull(coupon.getId()); // ID는 외부에서 부여되므로 null
        assertEquals(userId, coupon.getUserId());
        assertEquals(couponPolicyId, coupon.getCouponPolicyId());
        assertEquals(CouponStatus.ISSUED, coupon.getStatus());
    }

    @Test
    @DisplayName("쿠폰이 사용 가능한 상태일 때 isAvailable은 true를 반환한다")
    void 사용가능_쿠폰() {
        Coupon coupon = Coupon.of(1L, 100L, CouponStatus.ISSUED);
        assertTrue(coupon.isAvailable());
    }

    @Test
    @DisplayName("쿠폰이 이미 사용된 상태일 때 isAvailable은 false를 반환한다")
    void 사용불가_쿠폰() {
        Coupon coupon = Coupon.of(1L, 100L, CouponStatus.USED);
        assertFalse(coupon.isAvailable());
    }

    @Test
    @DisplayName("쿠폰을 사용하면 상태가 USED로 변경된다")
    void 쿠폰_사용() {
        Coupon coupon = Coupon.of(1L, 100L, CouponStatus.ISSUED);

        coupon.use();

        assertEquals(CouponStatus.USED, coupon.getStatus());
    }
}