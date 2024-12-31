package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserTransactionDTO {
    private Long id;
    private Long userId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionTime;
    private String status;
} 