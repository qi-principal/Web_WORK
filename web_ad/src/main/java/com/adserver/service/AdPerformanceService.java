package com.adserver.service;

import com.adserver.model.AdPerformance;
import com.adserver.repository.AdPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AdPerformanceService {
    @Autowired
    private AdPerformanceRepository performanceRepository;
    
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void updatePerformanceMetrics() {
        // 更新所有广告的统计信息
        performanceRepository.findAll().forEach(performance -> {
            // 计算转换率
            if (performance.getImpressions() > 0) {
                BigDecimal conversionRate = new BigDecimal(performance.getClicks())
                    .divide(new BigDecimal(performance.getImpressions()), 2, RoundingMode.HALF_UP);
                performance.setConversionRate(conversionRate);
                performanceRepository.save(performance);
            }
        });
    }
    
    public void recordImpression(Long adId) {
        AdPerformance performance = getOrCreatePerformance(adId);
        performance.setImpressions(performance.getImpressions() + 1);
        performanceRepository.save(performance);
    }
    
    public void recordClick(Long adId) {
        AdPerformance performance = getOrCreatePerformance(adId);
        performance.setClicks(performance.getClicks() + 1);
        performanceRepository.save(performance);
    }
    
    private AdPerformance getOrCreatePerformance(Long adId) {
        return performanceRepository.findByAdId(adId)
            .orElseGet(() -> {
                AdPerformance newPerformance = new AdPerformance();
                newPerformance.setAdId(adId);
                newPerformance.setImpressions(0);
                newPerformance.setClicks(0);
                newPerformance.setConversionRate(BigDecimal.ZERO);
                return newPerformance;
            });
    }
} 