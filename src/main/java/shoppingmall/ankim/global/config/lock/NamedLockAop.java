package shoppingmall.ankim.global.config.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.item.repository.ItemLockRepository;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class NamedLockAop {

    private final ItemLockRepository itemLockRepository;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(namedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        NamedLock namedLock = method.getAnnotation(NamedLock.class);

        // Generate lock key
        String key = NamedLockKeyGenerator.generate(signature.getParameterNames(),
                joinPoint.getArgs(), namedLock.key()).toString();

        log.info("Attempting to acquire MySQL Named Lock with key: {}", key);

        try {
            // Attempt to acquire the lock
            Long lockResult = itemLockRepository.getLock(key, namedLock.timeUnit().toSeconds(namedLock.timeout()));

            if (lockResult == null || lockResult == 0) {
                throw new RuntimeException("Failed to acquire lock for key: " + key + ". Timeout occurred.");
            }

            log.info("Lock acquired for key: {}", key);

            // Proceed with the business logic
            return aopForTransaction.proceed(joinPoint);
        } finally {
            // Release the lock
            itemLockRepository.releaseLock(key);
            log.info("Lock released for key: {}", key);
        }
    }
}
