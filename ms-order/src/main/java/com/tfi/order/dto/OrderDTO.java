package com.tfi.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;
}
