package shoppingmall.ankim.domain.viewRolling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.viewRolling.repository.ViewRollingRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ViewRollingService {

    private final ViewRollingRepository viewRollingRepository;

    public void increaseRealTimeViewCount(Long productNo) {
        viewRollingRepository.increaseRealTimeViewCount(productNo);
    }

    public void rollupRealTimeToDaily() {
        viewRollingRepository.rollupRealTimeToDaily();
    }

    public void rollupDailyToWeekly() {
        viewRollingRepository.rollupDailyToWeekly();
    }

    public void rollupWeeklyToMonthly() {
        viewRollingRepository.rollupWeeklyToMonthly();
    }

    public void initializeViewRolling(Long categoryNo, Long productNo) {
        viewRollingRepository.initializeViewRolling(categoryNo, productNo);
    }
}

