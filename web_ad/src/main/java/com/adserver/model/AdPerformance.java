package com.adserver.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ad_performance")
public class AdPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;
    
    private Long adId;
    
    private Integer impressions;
    
    private Integer clicks;
    
    private BigDecimal conversionRate;
} 