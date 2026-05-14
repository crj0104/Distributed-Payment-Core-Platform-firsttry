package com.payment.core.admin.feign;

import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-transaction-service", path = "/api/v1/refunds")
public interface RefundServiceClient {

    @GetMapping("/page")
    ApiResponse<?> pageRefunds(@RequestParam("current") int current,
                               @RequestParam("size") int size,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "transactionId", required = false) String transactionId);

    @GetMapping("/{refundId}")
    ApiResponse<?> getRefund(@PathVariable("refundId") String refundId);

    @PostMapping("/{refundId}/cancel")
    ApiResponse<?> cancelRefund(@PathVariable("refundId") String refundId);
}
