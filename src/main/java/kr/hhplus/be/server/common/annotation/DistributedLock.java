package kr.hhplus.be.server.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    /**
     * 락 키를 구성할 SpEL.
     * - 컬렉션/배열을 반환하면 각 요소별로 멀티락을 구성합니다.
     * - 복수 표현식을 줄 수 있습니다. (ex: {"#orderId", "#request.productIds"})
     */
    String[] keys();

    /** 키 prefix (ex: "stock"). */
    String prefix();

    /** 공정 락 여부 (기본: false). */
    boolean fair() default false;

    /** tryLock 대기 시간. */
    long waitTime() default 5L;

    /** 락 점유(lease) 시간. */
    long leaseTime() default 3L;

    /** 시간 단위. */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
