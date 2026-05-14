package com.payment.core.admin.controller;

import com.payment.core.admin.dto.request.AdminLoginRequestDTO;
import com.payment.core.admin.dto.response.AdminLoginResponseDTO;
import com.payment.core.common.dto.base.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AuthAdminController {

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponseDTO> login(@Valid @RequestBody AdminLoginRequestDTO request) {
        AdminLoginResponseDTO response = AdminLoginResponseDTO.builder()
                .token("admin-token-" + UUID.randomUUID())
                .userName(request.getUsername())
                .displayName("支付中台管理员")
                .roles(List.of("ADMIN_SUPER"))
                .permissions(List.of(
                        "dashboard:view",
                        "merchant:manage",
                        "transaction:manage",
                        "notification:manage",
                        "refund:manage",
                        "reconciliation:manage"
                ))
                .build();
        return ApiResponse.success(response);
    }
}
