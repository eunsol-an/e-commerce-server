package kr.hhplus.be.server.common.dto;

import java.time.LocalDateTime;

public class CouponDto {
    public record CouponIssueRequest(
            Long userId,
            Long couponId
    ) {}

    public record CouponIssueResponse(
            Long couponId,
            Integer discountAmount,
            LocalDateTime expiredAt
    ) {}

    public record CouponListResponse(
            Long couponId,
            Integer discountAmount,
            String status,
            LocalDateTime expiredAt
    ) {}
}
