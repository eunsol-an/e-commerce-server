package kr.hhplus.be.server.common.dto;

import java.time.LocalDateTime;

public class PointDto {
    public record PointChargeRequest(
            Long userId,
            Long amount
    ) {}

    public record BalanceResponse(
            Long userId,
            Long balance
    ) {}

    public record PointHistoryResponse(
            Long amount,
            String type, // CHARGE | USE
            LocalDateTime createdAt
    ) {}
}
