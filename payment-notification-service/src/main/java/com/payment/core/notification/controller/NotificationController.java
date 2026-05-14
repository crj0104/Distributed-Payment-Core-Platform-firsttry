package com.payment.core.notification.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.payment.core.common.dto.base.ApiResponse;
import com.payment.core.notification.dto.request.NotificationSendRequestDTO;
import com.payment.core.notification.dto.response.NotificationRecordDTO;
import com.payment.core.notification.dto.response.NotificationSendResultDTO;
import com.payment.core.notification.entity.NotificationRecord;
import com.payment.core.notification.mapper.NotificationRecordMapper;
import com.payment.core.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRecordMapper notificationRecordMapper;

    public NotificationController(NotificationService notificationService,
                                  NotificationRecordMapper notificationRecordMapper) {
        this.notificationService = notificationService;
        this.notificationRecordMapper = notificationRecordMapper;
    }

    @PostMapping
    public ApiResponse<NotificationSendResultDTO> sendNotification(@RequestBody NotificationSendRequestDTO body) {
        notificationService.sendNotification(
                body.getTransactionId(),
                body.getNotifyUrl(),
                body.getNotifyContent() != null ? body.getNotifyContent() : ""
        );
        return ApiResponse.success(NotificationSendResultDTO.builder().status("QUEUED").build());
    }

    @GetMapping("/page")
    public ApiResponse<Page<NotificationRecordDTO>> pageNotifications(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String notificationStatus,
            @RequestParam(required = false) String transactionId) {
        LambdaQueryWrapper<NotificationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(notificationStatus != null && !notificationStatus.isBlank(),
                        NotificationRecord::getNotificationStatus, notificationStatus)
                .eq(transactionId != null && !transactionId.isBlank(),
                        NotificationRecord::getTransactionId, transactionId)
                .orderByDesc(NotificationRecord::getCreatedAt);

        Page<NotificationRecord> page = notificationRecordMapper.selectPage(new Page<>(current, size), wrapper);
        Page<NotificationRecordDTO> result = new Page<>(current, size, page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toDTO).toList());
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationRecordDTO> getNotification(@PathVariable Long id) {
        NotificationRecord record = notificationRecordMapper.selectById(id);
        return ApiResponse.success(toDTO(record));
    }

    @PostMapping("/{id}/resend")
    public ApiResponse<NotificationSendResultDTO> resend(@PathVariable Long id) {
        NotificationRecord record = notificationRecordMapper.selectById(id);
        notificationService.sendNotification(
                record.getTransactionId(),
                record.getNotifyUrl(),
                record.getNotifyContent()
        );
        return ApiResponse.success(NotificationSendResultDTO.builder().status("RESENT").build());
    }

    private NotificationRecordDTO toDTO(NotificationRecord record) {
        return NotificationRecordDTO.builder()
                .id(record.getId())
                .transactionId(record.getTransactionId())
                .notifyUrl(record.getNotifyUrl())
                .notifyType(record.getNotifyType())
                .notificationStatus(record.getNotificationStatus())
                .retryCount(record.getRetryCount())
                .maxRetry(record.getMaxRetry())
                .lastHttpStatus(record.getLastHttpStatus())
                .failureReason(record.getFailureReason())
                .lastResponseBody(record.getLastResponseBody())
                .nextRetryAt(record.getNextRetryAt())
                .createdAt(record.getCreatedAt())
                .notifiedAt(record.getNotifiedAt())
                .build();
    }
}
