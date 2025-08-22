package kr.hhplus.be.server.domain.coupon.presentation;

import kr.hhplus.be.server.docs.CouponApiDocs;
import kr.hhplus.be.server.domain.coupon.application.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponApiDocs {
    private final CouponService couponService;

    @PostMapping("/issue")
    public ResponseEntity<String> issueCoupon(
            @RequestBody CouponDto.CouponIssueRequest request
    ) {
        boolean success = couponService.issue(request.userId(), request.couponPolicyId());

        if (success) {
            return ResponseEntity.ok("쿠폰 발급 성공! DB 반영은 대기열 처리 후 완료됩니다.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("발급 실패: 이미 발급되었거나 재고가 없습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<List<CouponDto.CouponListResponse>> getCoupons(
            @RequestParam Long userId
    ) {
        List<CouponDto.CouponListResponse> couponListResponseList = List.of(
                new CouponDto.CouponListResponse(1L, 1000, "ISSUED", LocalDateTime.now()),
                new CouponDto.CouponListResponse(2L, 500, "USED", LocalDateTime.now())
        );
        return ResponseEntity.ok(couponListResponseList);
    }
}
