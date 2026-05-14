package com.payment.core.admin.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardOverviewDTO {

    private long merchantCount;
    private long activeMerchantCount;
    private long transactionCount;
    private long notificationPendingCount;
    private long unresolvedDiffCount;
}
