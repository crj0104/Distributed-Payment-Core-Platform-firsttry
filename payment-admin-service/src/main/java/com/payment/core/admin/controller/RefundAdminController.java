package com.payment.core.admin.controller;

import com.payment.core.admin.feign.RefundServiceClient;
import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/refunds")
public class RefundAdminController {

    private final RefundServiceClient refundServiceClient;

    public RefundAdminController(RefundServiceClient refundServiceClient) {
        this.refundServiceClient = refundServiceClient;
    }

    @GetMapping
    public ApiResponse<?> pageRefunds(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String transactionId) {
        return refundServiceClient.pageRefunds(current, size, status, transactionId);
    }

    @GetMapping("/{refundId}")
    public ApiResponse<?> getRefund(@PathVariable String refundId) {
        return refundServiceClient.getRefund(refundId);
    }

    @PostMapping("/{refundId}/cancel")
    public ApiResponse<?> cancelRefund(@PathVariable String refundId) {
        return refundServiceClient.cancelRefund(refundId);
    }
}
