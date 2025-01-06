package shoppingmall.ankim.global.config.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class MailAsyncConfig {

    @Value("${mail.async.core-pool-size}")
    private int corePoolSize;

    @Value("${mail.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${mail.async.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "mailTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("MailSender-");
        executor.initialize();
        return executor;
    }
}