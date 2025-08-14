package kr.hhplus.be.server.common.lock;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockManager {
    private final RedissonClient redissonClient;

    public RedissonLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public AcquiredLock acquire(List<String> keys, boolean fair, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        if (keys == null || keys.isEmpty()) {
            return AcquiredLock.noop();
        }
        List<RLock> locks = new ArrayList<>(keys.size());
        for (String key : keys) {
            RLock lock = fair ? redissonClient.getFairLock(key) : redissonClient.getLock(key);
            locks.add(lock);
        }
        if (locks.size() == 1) {
            RLock lock = locks.get(0);
            boolean ok = lock.tryLock(waitTime, leaseTime, unit);
            return ok ? AcquiredLock.single(lock) : AcquiredLock.fail(keys);
        } else {
            RedissonMultiLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));
            boolean ok = multiLock.tryLock(waitTime, leaseTime, unit);
            return ok ? AcquiredLock.multi(multiLock, locks) : AcquiredLock.fail(keys);
        }
    }

    public record AcquiredLock(boolean acquired, RLock single, RedissonMultiLock multi, List<RLock> parts, List<String> keys) {
        public static AcquiredLock single(RLock single) { return new AcquiredLock(true, single, null, null, null); }
        public static AcquiredLock multi(RedissonMultiLock multi, List<RLock> parts) { return new AcquiredLock(true, null, multi, parts, null); }
        public static AcquiredLock fail(List<String> keys) { return new AcquiredLock(false, null, null, null, keys); }
        public static AcquiredLock noop() { return new AcquiredLock(true, null, null, null, null); }

        public void unlockQuietly() {
            try {
                if (multi != null) multi.unlock();
                else if (single != null && single.isHeldByCurrentThread()) single.unlock();
            } catch (Exception ignored) { }
        }
    }
}
