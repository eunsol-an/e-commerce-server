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

        Coupon coupon = Coupon.create(userId, couponPolicyId, CouponStatus.ISSUED);

        assertNull(coupon.getId()); // ID는 외부에서 부여되므로 null
        assertEquals(userId, coupon.getUserId());
        assertEquals(couponPolicyId, coupon.getCouponPolicyId());
        assertEquals(CouponStatus.ISSUED, coupon.getStatus());
    }

    @Test
    @DisplayName("쿠폰이 사용 가능한 상태일 때 isAvailable은 true를 반환한다")
    void 사용가능_쿠폰() {
        Coupon coupon = Coupon.create(1L, 100L, CouponStatus.ISSUED);
        assertTrue(coupon.isAvailable());
    }

    @Test
    @DisplayName("쿠폰이 이미 사용된 상태일 때 isAvailable은 false를 반환한다")
    void 사용불가_쿠폰() {
        Coupon coupon = Coupon.create(1L, 100L, CouponStatus.USED);
        assertFalse(coupon.isAvailable());
    }

    @Test
    @DisplayName("쿠폰을 사용하면 상태가 USED로 변경된다")
    void 쿠폰_사용() {
        Coupon coupon = Coupon.create(1L, 100L, CouponStatus.ISSUED);

        coupon.use();

        assertEquals(CouponStatus.USED, coupon.getStatus());
    }

    @Test
    @DisplayName("발급 수량이 남아 있으면 발급 가능하다")
    void 쿠폰_수량_충분() {
        CouponPolicy policy = CouponPolicy.of(1L, 100, 100, 99, 30);
        assertTrue(policy.isIssuable());
    }

    @Test
    @DisplayName("발급 수량이 초과되면 발급 불가하다")
    void 쿠폰_수량_초과() {
        CouponPolicy policy = CouponPolicy.of(1L, 100, 100, 100, 30);
        assertFalse(policy.isIssuable());
    }

    @Test
    @DisplayName("쿠폰 발급 수량을 1 증가시킨다")
    void 쿠폰_수량_증가() {
        CouponPolicy policy = CouponPolicy.of(1L, 100, 1, 0, 30);

        policy.increaseIssuedCount();

        assertEquals(1, policy.getIssuedCount());
    }
}