package com.f4pl0.pinnacle.portfolioservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateStockAssetDto {
    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    private long quantity;

    @NotNull(message = "Price must be provided")
    @Min(value = 0, message = "Price must be at least 0")
    private BigDecimal price;

    // No validation for purchaseTimestamp as it's optional
    private long purchaseTimestamp;
}