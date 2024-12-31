package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;
import java.util.Map;

@Data
@Builder
public class UserStatisticsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Map<Integer, Long> userTypeDistribution;
    private Double averageBalance;
} 