package kr.hhplus.be.server.domain.coupon.presentation;

import kr.hhplus.be.server.docs.CouponApiDocs;
import kr.hhplus.be.server.domain.coupon.application.CouponService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Void> issueCoupon(
            @RequestBody CouponDto.CouponIssueRequest request
    ) {
        couponService.issue(request.userId(), request.couponId());
        return ResponseEntity.ok().build();
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
