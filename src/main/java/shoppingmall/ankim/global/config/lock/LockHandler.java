package shoppingmall.ankim.global.config.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class LockHandler {
    private static final String LOCK_KEY_PREFIX = "LOCK_";
    private final ItemRepository itemRepository;

    public void lock(String key) {
        Long available = itemRepository.getLock(LOCK_KEY_PREFIX + key);
        if (available == 0) {
            throw new RuntimeException("LOCK_ACQUISITION_FAILED");
        }
        log.info("Lock acquired for key: {}", LOCK_KEY_PREFIX + key);
    }

    public void unlock(String key) {
        itemRepository.releaseLock(LOCK_KEY_PREFIX + key);
        log.info("Lock released for key: {}", LOCK_KEY_PREFIX + key);
    }
}
