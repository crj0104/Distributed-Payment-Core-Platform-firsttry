package com.payment.core.notification.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationRecordDTO {

    private Long id;
    private String transactionId;
    private String notifyUrl;
    private String notifyType;
    private String notificationStatus;
    private Integer retryCount;
    private Integer maxRetry;
    private Integer lastHttpStatus;
    private String failureReason;
    private String lastResponseBody;
    private LocalDateTime nextRetryAt;
    private LocalDateTime createdAt;
    private LocalDateTime notifiedAt;
}
