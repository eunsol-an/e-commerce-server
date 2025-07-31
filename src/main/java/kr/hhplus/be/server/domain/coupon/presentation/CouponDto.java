package kr.hhplus.be.server.domain.coupon.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CouponDto {
    @Schema(description = "쿠폰 발급 Request")
    public record CouponIssueRequest(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "쿠폰 ID", example = "10")
            Long couponPolicyId
    ) {}

    @Schema(description = "쿠폰 발급 Response")
    public record CouponIssueResponse(
            @Schema(description = "쿠폰 ID", example = "10")
            Long couponId,
            @Schema(description = "할인 금액", example = "1000")
            Integer discountAmount,
            @Schema(description = "만료일", example = "2025-07-25 00:00:00")
            LocalDateTime expiredAt
    ) {}

    @Schema(description = "보유 쿠폰 조회 Response")
    public record CouponListResponse(
            @Schema(description = "쿠폰 ID", example = "10")
            Long couponId,
            @Schema(description = "할인 금액", example = "1000")
            Integer discountAmount,
            @Schema(description = "쿠폰 상태", example = "ISSUED")
            String status,
            @Schema(description = "만료일", example = "2025-07-25 00:00:00")
            LocalDateTime expiredAt
    ) {}
}
