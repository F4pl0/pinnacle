package com.f4pl0.pinnacle.portfolioservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddStockAssetDto {
    @Pattern(regexp = "^[A-Z]{1,5}$")
    private String symbol;

    @NotNull
    @Min(0)
    private long quantity;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private long purchaseTimestamp;
}
