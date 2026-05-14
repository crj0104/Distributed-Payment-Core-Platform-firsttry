package com.payment.core.admin.feign;

import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 运营后台调用交易服务的 Feign Client
 */
@FeignClient(name = "payment-transaction-service", path = "/api/v1/payments")
public interface TransactionServiceClient {

    @GetMapping("/{transactionId}")
    ApiResponse<?> queryByTransactionId(@PathVariable("transactionId") String transactionId);

    @GetMapping
    ApiResponse<?> queryByOrderId(@RequestParam("orderId") String orderId);

    @GetMapping("/page")
    ApiResponse<?> queryPage(
            @RequestParam("current") int current,
            @RequestParam("size") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod);

    @PostMapping("/{transactionId}/close")
    ApiResponse<?> closePayment(@PathVariable("transactionId") String transactionId);
    
    @PostMapping("/{transactionId}/retry")
    ApiResponse<?> retryPayment(@PathVariable("transactionId") String transactionId);
}
