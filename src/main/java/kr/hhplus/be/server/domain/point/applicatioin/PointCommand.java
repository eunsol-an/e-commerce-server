package kr.hhplus.be.server.domain.point.applicatioin;

import kr.hhplus.be.server.domain.point.domain.model.UserPoint;

public class PointCommand {
    public record Charge(
            Long userId,
            long amount
    ) {
        public static Charge of(UserPoint userPoint) {
            return new Charge(userPoint.getId(), userPoint.getBalance());
        }

        public static Charge create(Long userId, long amount) {
            return new Charge(userId, amount);
        }
    }

    public record Use(
            Long userId,
            long amount
    ) {
        public static Use of(UserPoint userPoint) {
            return new Use(userPoint.getId(), userPoint.getBalance());
        }

        public static Use create(Long userId, Long amount) {
            return new Use(userId, amount);
        }
    }
}