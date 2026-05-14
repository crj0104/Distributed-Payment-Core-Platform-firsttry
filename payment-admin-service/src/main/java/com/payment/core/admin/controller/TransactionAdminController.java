package com.payment.core.admin.controller;

import com.payment.core.admin.feign.TransactionServiceClient;
import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.web.bind.annotation.*;

/**
 * 运营后台：交易管理控制器 (BFF 层)
 * 面向客服、财务、运营人员提供服务
 */
@RestController
@RequestMapping("/api/v1/admin/transactions")
public class TransactionAdminController {

    private final TransactionServiceClient transactionServiceClient;

    public TransactionAdminController(TransactionServiceClient transactionServiceClient) {
        this.transactionServiceClient = transactionServiceClient;
    }

    /**
     * 客服后台查询单笔交易详情
     */
    @GetMapping("/{transactionId}")
    public ApiResponse<?> queryTransaction(@PathVariable String transactionId) {
        // 调用底层核心交易服务
        return transactionServiceClient.queryByTransactionId(transactionId);
    }

    /**
     * 运营后台：分页条件查询交易流水列表回显
     */
    @GetMapping("/page")
    public ApiResponse<?> queryTransactionPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod) {
        return transactionServiceClient.queryPage(current, size, status, paymentMethod);
    }

    /**
     * 客服强制关闭卡单的交易
     */
    @PostMapping("/{transactionId}/close")
    public ApiResponse<?> closeTransaction(@PathVariable String transactionId) {
        // 调用底层核心交易服务关闭
        return transactionServiceClient.closePayment(transactionId);
    }

    /**
     * 运维手工重试回调或支付
     */
    @PostMapping("/{transactionId}/retry")
    public ApiResponse<?> retryTransaction(@PathVariable String transactionId) {
        return transactionServiceClient.retryPayment(transactionId);
    }
}
