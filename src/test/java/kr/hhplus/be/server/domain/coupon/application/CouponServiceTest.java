package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.Coupon;
import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CouponService 테스트")
@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 등록이 성공한다")
    void 쿠폰_등록_성공() {
        Long userId = 1L;
        Long couponPolicyId = 100L;

        CouponPolicy policy = CouponPolicy.of(couponPolicyId, 100, 1, 100, 365);
        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(policy));
        when(couponRepository.existsByUserIdAndCouponPolicyId(userId, policy.getId())).thenReturn(false);

        couponService.issue(userId, couponPolicyId);

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    @DisplayName("같은 쿠폰 정책으로 두 번 받으면 예외가 발생한다")
    void 쿠폰_중복지급_예외() {
        // given
        Long userId = 1L;
        Long couponPolicyId = 100L;
        CouponPolicy policy = CouponPolicy.of(couponPolicyId, 100, 1, 100, 365);

        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(policy));
        when(couponRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicyId)).thenReturn(true);

        // when & then
        assertThrows(ApiException.class, () -> {
            couponService.issue(userId, couponPolicyId);
        });
    }

}
