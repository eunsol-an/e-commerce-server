package kr.hhplus.be.server.domain.point.applicatioin;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;

public class PointInfo {
    public record Balance(
            Long userId,
            long amount
    ) {
        public static PointInfo.Balance of(Long userId, long amount) {
            return new PointInfo.Balance(userId, amount);
        }
    }
}
