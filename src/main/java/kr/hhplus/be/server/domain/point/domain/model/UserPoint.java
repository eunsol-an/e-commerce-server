package kr.hhplus.be.server.domain.point.domain.model;

import kr.hhplus.be.server.exception.ApiException;
import lombok.Builder;
import lombok.Getter;

import static kr.hhplus.be.server.exception.ErrorCode.*;

@Getter
public class UserPoint {
    private Long id;
    private long balance;

    private static final long MAX_BALANCE = 100_000;

    @Builder
    private UserPoint(Long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public static UserPoint of(Long id, long balance) {
        return UserPoint.builder()
                .id(id)
                .balance(balance)
                .build();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new ApiException(USER_POINT_INVALID_CHARGE_AMOUNT);
        }
        if (this.balance + amount > MAX_BALANCE) {
            throw new ApiException(USER_POINT_BALANCE_LIMIT_EXCEEDED);
        }
        this.balance += amount;
    }

    public void use(long amount) {
        if (amount <= 0) {
            throw new ApiException(USER_POINT_INVALID_USE_AMOUNT);
        }
        if (this.balance < amount) {
            throw new ApiException(USER_POINT_INSUFFICIENT_BALANCE);
        }
        this.balance -= amount;
    }

    public boolean isInsufficientBalance(long amount) {
        return this.balance < amount;
    }
}
