package shoppingmall.ankim.global.flag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class TaskCompletionHandler {
    private final Map<String, LocalDateTime> taskCompletionMap = new ConcurrentHashMap<>();
    private final AtomicInteger skippedCount = new AtomicInteger(0);

    public boolean isTaskAlreadyCompleted(String taskName, LocalDateTime now) {
        boolean alreadyCompleted = taskCompletionMap.containsKey(taskName)
                && taskCompletionMap.get(taskName).toLocalDate().isEqual(now.toLocalDate());
        if (alreadyCompleted) {
            skippedCount.incrementAndGet(); // 작업 스킵 카운트 증가
        }
        return alreadyCompleted;
    }

    public void markTaskAsCompleted(String taskName, LocalDateTime completedAt) {
        taskCompletionMap.put(taskName, completedAt);
    }

    public int getSkippedCount() {
        return skippedCount.get();
    }
}

