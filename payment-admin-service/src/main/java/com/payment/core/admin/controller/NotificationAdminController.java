package com.payment.core.admin.controller;

import com.payment.core.admin.feign.NotificationServiceClient;
import com.payment.core.common.dto.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class NotificationAdminController {

    private final NotificationServiceClient notificationServiceClient;

    public NotificationAdminController(NotificationServiceClient notificationServiceClient) {
        this.notificationServiceClient = notificationServiceClient;
    }

    @GetMapping
    public ApiResponse<?> pageNotifications(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String notificationStatus,
            @RequestParam(required = false) String transactionId) {
        return notificationServiceClient.pageNotifications(current, size, notificationStatus, transactionId);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getNotification(@PathVariable Long id) {
        return notificationServiceClient.getNotification(id);
    }

    @PostMapping("/{id}/resend")
    public ApiResponse<?> resend(@PathVariable Long id) {
        return notificationServiceClient.resend(id);
    }
}
