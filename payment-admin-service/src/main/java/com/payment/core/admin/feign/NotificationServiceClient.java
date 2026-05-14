package com.payment.core.admin.feign;

import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-notification-service", path = "/api/v1/notifications")
public interface NotificationServiceClient {

    @GetMapping("/page")
    ApiResponse<?> pageNotifications(@RequestParam("current") int current,
                                     @RequestParam("size") int size,
                                     @RequestParam(value = "notificationStatus", required = false) String notificationStatus,
                                     @RequestParam(value = "transactionId", required = false) String transactionId);

    @GetMapping("/{id}")
    ApiResponse<?> getNotification(@PathVariable("id") Long id);

    @PostMapping("/{id}/resend")
    ApiResponse<?> resend(@PathVariable("id") Long id);
}
