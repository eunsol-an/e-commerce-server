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

import static kr.hhplus.be.server.exception.ErrorCode.ALREADY_ISSUED_COUPON;
import static kr.hhplus.be.server.exception.ErrorCode.COUPON_SOLD_OUT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        // 발급 가능 (issuedCount < totalQuantity)
        CouponPolicy policy = CouponPolicy.of(couponPolicyId, 100, 1, 0, 365);
        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(policy));
        when(couponRepository.existsByUserIdAndCouponPolicyId(userId, policy.getId())).thenReturn(false);

        couponService.issue(userId, couponPolicyId);

        verify(couponRepository, times(1)).save(any(Coupon.class));
        verify(couponPolicyRepository, times(1)).save(policy);
        assertThat(policy.getIssuedCount()).isEqualTo(1); // 수량이 1 증가했는지 확인
    }

    @Test
    @DisplayName("같은 쿠폰 정책으로 두 번 받으면 예외가 발생한다")
    void 쿠폰_중복지급_예외() {
        // given
        Long userId = 1L;
        Long couponPolicyId = 100L;

        CouponPolicy policy = CouponPolicy.of(couponPolicyId, 100, 1, 0, 365);
        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(policy));
        when(couponRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicyId)).thenReturn(true);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            couponService.issue(userId, couponPolicyId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ALREADY_ISSUED_COUPON);
    }

    @Test
    @DisplayName("쿠폰 발급 수량이 초과되면 예외가 발생한다")
    void 쿠폰_수량초과_예외() {
        // given
        Long userId = 1L;
        Long couponPolicyId = 100L;

        CouponPolicy policy = CouponPolicy.of(couponPolicyId, 100, 100, 100, 365);
        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(policy));

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            couponService.issue(userId, couponPolicyId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(COUPON_SOLD_OUT);
    }

}
