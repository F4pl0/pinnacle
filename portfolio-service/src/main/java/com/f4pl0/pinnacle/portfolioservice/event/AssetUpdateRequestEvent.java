package com.f4pl0.pinnacle.portfolioservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdateRequestEvent {
    private String symbol;
    private AssetType assetType;

    public enum AssetType {
        STOCK;
    }
}
