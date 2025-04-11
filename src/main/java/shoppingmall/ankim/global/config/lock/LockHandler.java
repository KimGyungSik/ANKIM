package shoppingmall.ankim.global.config.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.item.repository.ItemLockRepository;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class LockHandler {

    private static final String LOCK_KEY_PREFIX = "LOCK_";
    private final ItemLockRepository itemLockRepository;
    private final RedissonClient redissonClient;

//    public void lock(String key) {
//        Long available = itemLockRepository.getLock(LOCK_KEY_PREFIX + key);
////        락을 얻을 수 있을 때까지 최대 30초 동안 대기합니다.
////        락이 사용 중이라면 30초 후에 타임아웃이 발생하고 0을 반환합니다.
//        if (available == 0) {
//            throw new RuntimeException("LOCK_ACQUISITION_FAILED");
//        }
//        log.info("Lock acquired for key: {}", LOCK_KEY_PREFIX + key);
//    }

    public void unlock(String key) {
        itemLockRepository.releaseLock(LOCK_KEY_PREFIX + key);
        log.info("Lock released for key: {}", LOCK_KEY_PREFIX + key);
    }

    public void lockWithRedisson(String key, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + key);
        try {
            boolean available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if(!available) {
                throw new InterruptedException("LOCK_ACQUISITION_FAILED");
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void unlockWithRedisson(String key) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + key);
        lock.unlock();
    }
}
