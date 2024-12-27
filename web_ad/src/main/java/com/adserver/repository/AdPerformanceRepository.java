package com.adserver.repository;

import com.adserver.model.AdPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdPerformanceRepository extends JpaRepository<AdPerformance, Long> {
    
    // 根据广告ID查找性能统计数据
    Optional<AdPerformance> findByAdId(Long adId);
    
    // 根据广告ID删除性能统计数据
    void deleteByAdId(Long adId);
    
    // 查找点击率大于指定值的广告性能数据
    List<AdPerformance> findByConversionRateGreaterThan(BigDecimal rate);
    
    // 查找展示次数大于指定值的广告性能数据
    List<AdPerformance> findByImpressionsGreaterThan(Integer impressions);
    
    // 查找点击次数大于指定值的广告性能数据
    List<AdPerformance> findByClicksGreaterThan(Integer clicks);
} 