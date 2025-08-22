package kr.hhplus.be.server.common.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public enum RedisKeys {
    PRODUCT_RANKING("product:ranking", Duration.ofDays(7)),
    PRODUCT_RANKING_3DAYS("product:ranking:3days", Duration.ofMinutes(30)),
    ;

    private final String key;
    private final Duration ttl;
}
