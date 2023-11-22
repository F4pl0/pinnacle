package com.f4pl0.pinnacle.portfolioservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddStockAssetDto {
    @Pattern(regexp = "^[A-Z]{1,5}$", message = "Symbol must be 1 to 5 uppercase letters")
    private String symbol;

    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    private long quantity;

    @NotNull(message = "Price must be provided")
    @Min(value = 0, message = "Price must be at least 0")
    private BigDecimal price;

    // No validation for purchaseTimestamp as it's optional
    private long purchaseTimestamp;
}
