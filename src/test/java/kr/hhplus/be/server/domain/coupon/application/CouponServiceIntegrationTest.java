package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.domain.model.CouponPolicy;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponPolicyRepository;
import kr.hhplus.be.server.domain.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.exception.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("CouponService 통합 테스트")
public class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Long userId;
    private CouponPolicy couponPolicy;

    @BeforeEach
    void setUp() {
        userId = 1L;
        couponPolicy = couponPolicyRepository.save(
                CouponPolicy.of(null, 1000, 500, 0, 30)
        );
    }

    @Test
    @DisplayName("쿠폰이 정상 발급된다")
    void 쿠폰_발급_성공() {
        // when
        couponService.issue(userId, couponPolicy.getId());

        // then
        boolean exists = couponRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicy.getId());
        assertThat(exists).isTrue();

        CouponPolicy updatedPolicy = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();
        assertThat(updatedPolicy.getIssuedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("같은 쿠폰을 두 번 발급받을 수 없다")
    void 쿠폰_중복발급_예외() {
        // given
        couponService.issue(userId, couponPolicy.getId());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            couponService.issue(userId, couponPolicy.getId());
        });
        assertThat(exception.getErrorCode()).isEqualTo(ALREADY_ISSUED_COUPON);
    }

    @Test
    @DisplayName("쿠폰 발급 수량을 초과하면 예외가 발생한다")
    void 쿠폰_수량초과_예외() {
        // given
        CouponPolicy soldOutPolicy = couponPolicyRepository.save(
                CouponPolicy.of(null, 100, 100, 100, 30)
        );

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {
            couponService.issue(userId, soldOutPolicy.getId());
        });
        assertThat(exception.getErrorCode()).isEqualTo(COUPON_SOLD_OUT);
    }
}
