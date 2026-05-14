package com.payment.core.testclient.controller;

import com.payment.core.common.dto.base.ApiResponse;
import com.payment.core.testclient.dto.request.GatewayCreatePaymentRequestDTO;
import com.payment.core.testclient.dto.response.GatewayPaymentResultDTO;
import com.payment.core.testclient.dto.response.GatewayTransactionQueryDTO;
import com.payment.core.testclient.feign.PaymentCoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 模拟商户端：电商订单与收银台模拟控制器
 */
@Slf4j
@RestController
@RequestMapping("/mock/mall")
public class MockOrderController {

    private final PaymentCoreClient paymentCoreClient;

    public MockOrderController(PaymentCoreClient paymentCoreClient) {
        this.paymentCoreClient = paymentCoreClient;
    }

    /**
     * 1. 模拟用户在电商点击“购买”
     * 生成商城订单，并调用支付中台创建支付单
     */
    @PostMapping("/buy")
    public ApiResponse<GatewayPaymentResultDTO> buyProduct(@RequestParam String productName,
                                                           @RequestParam BigDecimal price,
                                                           @RequestParam String paymentMethod) {
        String mockOrderId = "MALL_ORDER_" + System.currentTimeMillis();
        log.info("电商生成业务订单: orderId={}, product={}, price={}", mockOrderId, productName, price);

        // 构造调用支付中台的请求
        GatewayCreatePaymentRequestDTO createReq = GatewayCreatePaymentRequestDTO.builder()
                .orderId(mockOrderId)
                .amount(price)
                .currency("CNY")
                .paymentMethod(paymentMethod)
                .idempotentKey(UUID.randomUUID().toString())
                .description("购买商品: " + productName)
                .notifyUrl("http://127.0.0.1:8088/mock/callback/payment-notify")
                .build();
        
        log.info("准备调用支付中台下单接口...");
        ApiResponse<GatewayPaymentResultDTO> gatewayResponse = paymentCoreClient.createPayment(createReq);
        log.info("支付中台返回: {}", gatewayResponse);

        return gatewayResponse;
    }

    /**
     * 2. 模拟收银台发起支付执行
     */
    @PostMapping("/pay/{transactionId}")
    public ApiResponse<GatewayPaymentResultDTO> pay(@PathVariable String transactionId) {
        log.info("收银台发起支付执行: transactionId={}", transactionId);
        ApiResponse<GatewayPaymentResultDTO> gatewayResponse = paymentCoreClient.executePayment(transactionId);
        log.info("支付中台执行结果返回: {}", gatewayResponse);
        return gatewayResponse;
    }

    /**
     * 3. 模拟前端轮询查询支付结果
     */
    @GetMapping("/query/{transactionId}")
    public ApiResponse<GatewayTransactionQueryDTO> query(@PathVariable String transactionId) {
        log.info("前端轮询查询支付单: transactionId={}", transactionId);
        return paymentCoreClient.queryPayment(transactionId);
    }
}
