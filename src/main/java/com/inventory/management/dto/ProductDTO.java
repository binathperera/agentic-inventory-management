package com.inventory.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductDTO {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "Product name is required")
    private String name;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private float latestUnitPrice;
    private String latestBatchNo;
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer remainingQuantity;
}
