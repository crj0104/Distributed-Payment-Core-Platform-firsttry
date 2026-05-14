package com.payment.core.admin.controller;

import com.payment.core.admin.dto.request.ReconciliationTriggerRequestDTO;
import com.payment.core.admin.feign.ReconciliationServiceClient;
import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reconciliations")
public class ReconciliationAdminController {

    private final ReconciliationServiceClient reconciliationServiceClient;

    public ReconciliationAdminController(ReconciliationServiceClient reconciliationServiceClient) {
        this.reconciliationServiceClient = reconciliationServiceClient;
    }

    @GetMapping
    public ApiResponse<?> pageTasks(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String provider) {
        return reconciliationServiceClient.pageTasks(current, size, status, provider);
    }

    @GetMapping("/{batchNo}")
    public ApiResponse<?> getTask(@PathVariable String batchNo) {
        return reconciliationServiceClient.queryByBatchNo(batchNo);
    }

    @GetMapping("/{batchNo}/diffs")
    public ApiResponse<?> getDiffs(@PathVariable String batchNo) {
        return reconciliationServiceClient.queryDiffs(batchNo);
    }

    @PostMapping("/trigger")
    public ApiResponse<?> trigger(@RequestBody ReconciliationTriggerRequestDTO request) {
        return reconciliationServiceClient.trigger(request);
    }
}
