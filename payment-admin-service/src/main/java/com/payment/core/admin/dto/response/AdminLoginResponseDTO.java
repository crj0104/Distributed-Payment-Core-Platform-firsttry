package com.payment.core.admin.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminLoginResponseDTO {

    private String token;
    private String userName;
    private String displayName;
    private List<String> roles;
    private List<String> permissions;
}
