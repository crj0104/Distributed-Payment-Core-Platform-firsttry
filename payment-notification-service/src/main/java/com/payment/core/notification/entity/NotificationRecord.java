package com.payment.core.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("payment_notification")
public class NotificationRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String transactionId;

    private String notifyUrl;

    private String notifyType;

    private String notifyContent;

    private String notificationStatus;

    private Integer retryCount;

    private Integer maxRetry;

    private String lastResponseBody;

    private Integer lastHttpStatus;

    private String failureReason;

    private LocalDateTime nextRetryAt;

    private LocalDateTime createdAt;

    private LocalDateTime notifiedAt;
}
