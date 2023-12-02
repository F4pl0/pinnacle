package com.f4pl0.pinnacle.portfolioservice.dto.stock;

import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class StockAssetResponseDto {
    private UUID id;
    private String symbol;
    private long quantity;
    private BigDecimal price;
    private long purchaseTimestamp;

    static public StockAssetResponseDto fromStockAsset(StockAsset stockAsset) {
        StockAssetResponseDto stockAssetResponseDto = new StockAssetResponseDto();
        stockAssetResponseDto.setId(stockAsset.getId());
        stockAssetResponseDto.setSymbol(stockAsset.getSymbol());
        stockAssetResponseDto.setQuantity(stockAsset.getQuantity());
        stockAssetResponseDto.setPrice(stockAsset.getPrice());
        stockAssetResponseDto.setPurchaseTimestamp(stockAsset.getPurchaseTimestamp());
        return stockAssetResponseDto;
    }
}
