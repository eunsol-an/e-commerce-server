package kr.hhplus.be.server.common.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public enum RedisKeys {
    PRODUCT_RANKING("product:ranking", Duration.ofDays(7)),
    PRODUCT_RANKING_3DAYS("product:ranking:3days", Duration.ofMinutes(30)),
    COUPON_STOCK("coupon:%d:stock", null),
    COUPON_USERS("coupon:%d:users", null),
    COUPON_QUEUE("coupon:%d:queue", null)
    ;

    private final String key;
    private final Duration ttl;

    public String format(Object... args) {
        return String.format(key, args);
    }
}
