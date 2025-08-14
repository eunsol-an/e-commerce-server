package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.annotation.DistributedLock;
import kr.hhplus.be.server.common.lock.LockKeyResolver;
import kr.hhplus.be.server.common.lock.RedissonLockManager;
import kr.hhplus.be.server.exception.LockAcquisitionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {
    private final RedissonLockManager lockManager;

    public DistributedLockAspect(RedissonLockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Around("@annotation(kr.hhplus.be.server.common.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        DistributedLock ann = method.getAnnotation(DistributedLock.class);

        List<String> keys = LockKeyResolver.resolveKeys(method, pjp.getArgs(), ann.prefix(), ann.keys());

        RedissonLockManager.AcquiredLock acquired;
        try {
            acquired = lockManager.acquire(keys, ann.fair(), ann.waitTime(), ann.leaseTime(), ann.timeUnit());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException("락 대기 중 인터럽트 발생", e);
        }

        if (!acquired.acquired()) {
            throw new LockAcquisitionException("락 획득 실패: " + keys);
        }

        try {
            // 이 시점 이후에 @Transactional 프록시가 동작 → 트랜잭션 시작
            return pjp.proceed();
        } finally {
            acquired.unlockQuietly();
        }
    }
}
