package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.dto.CouponDto;
import kr.hhplus.be.server.docs.CouponApiDocs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController implements CouponApiDocs {

    @PostMapping("/issue")
    public ResponseEntity<CouponDto.CouponIssueResponse> issueCoupon(
            @RequestBody CouponDto.CouponIssueRequest request
    ) {
        return ResponseEntity.ok(new CouponDto.CouponIssueResponse(1L, 1000, LocalDateTime.now()));
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
