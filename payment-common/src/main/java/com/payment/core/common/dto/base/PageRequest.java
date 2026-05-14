package com.payment.core.common.dto.base;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageRequest {

    @Min(value = 1, message = "page must be at least 1")
    private int page = 1;

    @Min(value = 1, message = "size must be at least 1")
    @Max(value = 100, message = "size must be at most 100")
    private int size = 20;

    public int getOffset() {
        return (page - 1) * size;
    }
}
