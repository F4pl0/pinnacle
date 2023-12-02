package com.f4pl0.pinnacle.portfolioservice.dto.stock;

import com.f4pl0.pinnacle.portfolioservice.dto.PastOrPresentTimestamp;
import jakarta.validation.constraints.DecimalMin;
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
    @DecimalMin(value = "0.001", message = "Price must be at least 0.001")
    private BigDecimal price;

    @PastOrPresentTimestamp(message = "Purchase timestamp must be in the past or present")
    private long purchaseTimestamp;
}
