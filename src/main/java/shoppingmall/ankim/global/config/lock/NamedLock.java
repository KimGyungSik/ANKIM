package shoppingmall.ankim.global.config.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedLock {

    /**
     * The key for the named lock.
     */
    String key();

    /**
     * Timeout duration for acquiring the lock.
     */
    long timeout() default 5L;

    /**
     * Time unit for the timeout duration.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}

