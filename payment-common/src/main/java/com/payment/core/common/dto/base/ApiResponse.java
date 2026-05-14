package com.payment.core.common.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;
import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private String requestId;
    private String traceId;
    private Instant timestamp;
    private T data;

    private ApiResponse() {
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 200;
        response.message = "SUCCESS";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String requestId, String traceId) {
        ApiResponse<T> response = success(data);
        response.requestId = requestId;
        response.traceId = traceId;
        return response;
    }

    public static <T> ApiResponse<T> created(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 201;
        response.message = "CREATED";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> accepted(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 202;
        response.message = "ACCEPTED";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 500; // default http status for legacy support
        response.message = message;
        // 在返回错误时，统一注入当前的 traceId
        response.traceId = com.payment.core.common.trace.TraceContext.getTraceId();
        return response;
    }

    public static <T> ApiResponse<T> error(int httpStatus, String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = httpStatus;
        response.message = "[" + code + "] " + message;
        response.traceId = com.payment.core.common.trace.TraceContext.getTraceId();
        return response;
    }

    public static <T> ApiResponse<T> error(int httpStatus, String code, String message, String requestId) {
        ApiResponse<T> response = error(httpStatus, code, message);
        response.requestId = requestId;
        return response;
    }
}
