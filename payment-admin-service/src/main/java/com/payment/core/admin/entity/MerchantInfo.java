package com.payment.core.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant_info")
public class MerchantInfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String merchantNo;
    private String merchantName;
    private String apiKey;
    private String apiSecret;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
