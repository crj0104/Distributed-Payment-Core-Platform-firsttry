package com.payment.core.admin.feign;

import com.payment.core.admin.dto.request.ReconciliationTriggerRequestDTO;
import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-reconciliation-service", path = "/api/v1/reconciliations")
public interface ReconciliationServiceClient {

    @GetMapping("/page")
    ApiResponse<?> pageTasks(@RequestParam("current") int current,
                             @RequestParam("size") int size,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "provider", required = false) String provider);

    @GetMapping("/{batchNo}")
    ApiResponse<?> queryByBatchNo(@PathVariable("batchNo") String batchNo);

    @GetMapping("/{batchNo}/diffs")
    ApiResponse<?> queryDiffs(@PathVariable("batchNo") String batchNo);

    @PostMapping
    ApiResponse<?> trigger(@RequestBody ReconciliationTriggerRequestDTO request);
}
