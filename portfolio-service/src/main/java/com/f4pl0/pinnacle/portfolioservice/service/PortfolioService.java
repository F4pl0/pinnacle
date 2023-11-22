package com.f4pl0.pinnacle.portfolioservice.service;

import com.f4pl0.pinnacle.portfolioservice.dto.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.event.AssetUpdateRequestEvent;
import com.f4pl0.pinnacle.portfolioservice.exception.StockAssetException;
import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import com.f4pl0.pinnacle.portfolioservice.repository.StockAssetRepository;
import io.github.f4pl0.IEXCloudClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final StockAssetRepository stockAssetRepository;
    private final IEXCloudClient iexCloudClient;
    private final KafkaTemplate<String, AssetUpdateRequestEvent> assetUpdateRequestTemplate;

    @SneakyThrows
    public void addStockAsset(String userEmail, AddStockAssetDto addStockAssetDto) {
        // Check if the stock asset is valid
        AtomicBoolean isValid = new AtomicBoolean(false);
        iexCloudClient.reference.dailyIEXTradingSymbols().forEach(symbol -> {
            if (symbol.symbol.equals(addStockAssetDto.getSymbol())) {
                isValid.set(true);
                return;
            }
        });

        if (!isValid.get()) {
            throw new StockAssetException("Invalid stock asset");
        }

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol(addStockAssetDto.getSymbol());
        stockAsset.setQuantity(addStockAssetDto.getQuantity());
        stockAsset.setPrice(addStockAssetDto.getPrice());
        stockAsset.setPurchaseTimestamp(addStockAssetDto.getPurchaseTimestamp());
        stockAssetRepository.save(stockAsset);

        AssetUpdateRequestEvent assetUpdateRequestEvent = new AssetUpdateRequestEvent();
        assetUpdateRequestEvent.setSymbol(addStockAssetDto.getSymbol());
        assetUpdateRequestEvent.setAssetType(AssetUpdateRequestEvent.AssetType.STOCK);
        assetUpdateRequestTemplate.send("asset-update-request-topic", assetUpdateRequestEvent);
    }
}
