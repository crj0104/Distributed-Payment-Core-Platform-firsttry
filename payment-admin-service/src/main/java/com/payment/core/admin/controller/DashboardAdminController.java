package com.payment.core.admin.controller;

import com.payment.core.admin.dto.response.DashboardOverviewDTO;
import com.payment.core.admin.service.MerchantAdminService;
import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class DashboardAdminController {

    private final MerchantAdminService merchantAdminService;

    public DashboardAdminController(MerchantAdminService merchantAdminService) {
        this.merchantAdminService = merchantAdminService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewDTO> overview() {
        long merchantCount = merchantAdminService.countMerchants();
        DashboardOverviewDTO overview = DashboardOverviewDTO.builder()
                .merchantCount(merchantCount)
                .activeMerchantCount(merchantCount)
                .transactionCount(0)
                .notificationPendingCount(0)
                .unresolvedDiffCount(0)
                .build();
        return ApiResponse.success(overview);
    }
}
