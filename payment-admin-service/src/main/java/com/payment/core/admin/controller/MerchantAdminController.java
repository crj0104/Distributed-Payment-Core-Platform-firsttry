package com.payment.core.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.payment.core.admin.dto.request.CreateMerchantRequestDTO;
import com.payment.core.admin.dto.response.MerchantDetailDTO;
import com.payment.core.admin.dto.response.MerchantSecretResetDTO;
import com.payment.core.admin.dto.response.MerchantSummaryDTO;
import com.payment.core.admin.service.MerchantAdminService;
import com.payment.core.common.dto.base.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运营后台：商户配置管理控制器
 */
@RestController
@RequestMapping("/api/v1/admin/merchants")
public class MerchantAdminController {

    private final MerchantAdminService merchantAdminService;

    public MerchantAdminController(MerchantAdminService merchantAdminService) {
        this.merchantAdminService = merchantAdminService;
    }

    @PostMapping
    public ApiResponse<MerchantDetailDTO> createMerchant(@Valid @RequestBody CreateMerchantRequestDTO request) {
        return ApiResponse.success(merchantAdminService.createMerchant(request));
    }

    @GetMapping
    public ApiResponse<Page<MerchantSummaryDTO>> pageMerchants(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(merchantAdminService.pageMerchants(current, size, keyword, status));
    }

    @GetMapping("/{merchantNo}")
    public ApiResponse<MerchantDetailDTO> getMerchant(@PathVariable String merchantNo) {
        return ApiResponse.success(merchantAdminService.getMerchant(merchantNo));
    }

    @PostMapping("/{merchantNo}/freeze")
    public ApiResponse<MerchantDetailDTO> freezeMerchant(@PathVariable String merchantNo) {
        return ApiResponse.success(merchantAdminService.updateStatus(merchantNo, "FROZEN"));
    }

    @PostMapping("/{merchantNo}/unfreeze")
    public ApiResponse<MerchantDetailDTO> unfreezeMerchant(@PathVariable String merchantNo) {
        return ApiResponse.success(merchantAdminService.updateStatus(merchantNo, "ACTIVE"));
    }

    @PostMapping("/{merchantNo}/reset-secret")
    public ApiResponse<MerchantSecretResetDTO> resetSecret(@PathVariable String merchantNo) {
        return ApiResponse.success(merchantAdminService.resetSecret(merchantNo));
    }
}
