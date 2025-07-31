package kr.hhplus.be.server.domain.point.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.applicatioin.PointCommand;
import kr.hhplus.be.server.domain.point.applicatioin.PointInfo;

import java.time.LocalDateTime;

public class PointDto {
    @Schema(description = "포인트 충전 Request")
    public record PointChargeRequest(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "충전 금액", example = "1000")
            Long amount
    ) {
        public PointCommand.Charge toCommand() {
            return PointCommand.Charge.create(this.userId, this.amount);
        }
    }

    @Schema(description = "포인트 사용 Request")
    public record PointUseRequest(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "사용 금액", example = "1000")
            Long amount
    ) {
        public PointCommand.Use toCommand() {
            return PointCommand.Use.create(this.userId, this.amount);
        }
    }

    @Schema(description = "잔액 Response")
    public record BalanceResponse(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "잔액", example = "1000")
            Long balance
    ) {
        public static PointDto.BalanceResponse of(PointInfo.Balance balance) {
            return new PointDto.BalanceResponse(balance.userId(), balance.amount());
        }
    }

    @Schema(description = "포인트 내역 조회 Response")
    public record PointHistoryResponse(
            @Schema(description = "금액", example = "1000")
            Long amount,
            @Schema(description = "타입", example = "CHARGE")
            String type, // CHARGE | USE
            @Schema(description = "일시", example = "2025-07-18 04:53:44")
            LocalDateTime createdAt
    ) {}
}
