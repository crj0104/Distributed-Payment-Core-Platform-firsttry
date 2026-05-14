package com.payment.core.reconciliation.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ReconciliationSummaryDTO {

    private String batchNo;

    private LocalDate reconciliationDate;

    private String provider;

    private int totalCount;

    private int providerTotalCount;

    private int matchCount;

    private int diffCount;

    private int missingInPlatform;

    private int missingInProvider;

    private BigDecimal totalPlatformAmount;

    private BigDecimal totalProviderAmount;

    private BigDecimal diffAmount;

    private String status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;
}
