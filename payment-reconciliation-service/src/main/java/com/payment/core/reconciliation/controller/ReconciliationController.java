package com.payment.core.reconciliation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.payment.core.common.dto.base.ApiResponse;
import com.payment.core.reconciliation.dto.ReconciliationTriggerRequestDTO;
import com.payment.core.reconciliation.dto.ReconciliationSummaryDTO;
import com.payment.core.reconciliation.service.ReconciliationService;
import com.payment.core.reconciliation.vo.ReconciliationDiffVO;
import com.payment.core.reconciliation.vo.ReconciliationTaskVO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reconciliations")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    /**
     * POST /api/v1/reconciliations
     *
     * 手动触发对账
     */
    @PostMapping
    public ApiResponse<ReconciliationSummaryDTO> reconcile(@RequestBody ReconciliationTriggerRequestDTO body) {
        String provider = body.getProvider() != null ? body.getProvider() : "MOCK";
        String dateStr = body.getDate() != null ? body.getDate() : LocalDate.now().toString();
        ReconciliationSummaryDTO result = reconciliationService.runReconciliation(
                provider, LocalDate.parse(dateStr));
        return ApiResponse.success(result);
    }


    /**
     * GET /api/v1/reconciliations/{batchNo}
     *
     * 按批次号查询对账结果
     */
    @GetMapping("/{batchNo}")
    public ApiResponse<ReconciliationSummaryDTO> queryByBatchNo(@PathVariable String batchNo) {
        ReconciliationSummaryDTO result = reconciliationService.queryByBatchNo(batchNo);
        return ApiResponse.success(result);
    }

    @GetMapping("/page")
    public ApiResponse<Page<ReconciliationTaskVO>> pageTasks(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String provider) {
        return ApiResponse.success(reconciliationService.pageTasks(current, size, status, provider));
    }

    @GetMapping("/{batchNo}/diffs")
    public ApiResponse<List<ReconciliationDiffVO>> queryDiffs(@PathVariable String batchNo) {
        return ApiResponse.success(reconciliationService.queryDiffs(batchNo));
    }

    /**
     * POST /api/v1/reconciliations/rapid
     * POST /api/v1/reconciliations/rapid
     *
     * 快速对账（仅扫描，不记录批次）
     */
    @PostMapping("/rapid")
    public ApiResponse<ReconciliationSummaryDTO> rapidReconcile() {
        ReconciliationSummaryDTO result = reconciliationService.runReconciliation(
                "MOCK", LocalDate.now());
        return ApiResponse.success(result);
    }
}
