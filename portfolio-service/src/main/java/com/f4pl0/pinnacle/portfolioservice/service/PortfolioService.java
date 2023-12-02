package com.f4pl0.pinnacle.portfolioservice.service;

import com.f4pl0.pinnacle.portfolioservice.dto.stock.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.StockAssetResponseDto;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.UpdateStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.event.AssetUpdateRequestEvent;
import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import com.f4pl0.pinnacle.portfolioservice.repository.StockAssetRepository;
import io.github.f4pl0.IEXCloudClient;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing portfolio operations.
 */
@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final StockAssetRepository stockAssetRepository;
    private final IEXCloudClient iexCloudClient;
    private final KafkaTemplate<String, AssetUpdateRequestEvent> assetUpdateRequestTemplate;
    private final Validator validator;

    /**
     * Gets all stock assets of the user.
     *
     * @param userEmail The email of the user.
     * @return A list of StockAssetResponseDto objects.
     */
    public List<StockAssetResponseDto> getAllStockAssets(String userEmail) {
        return stockAssetRepository.findByUserEmail(userEmail)
                .stream()
                .map(StockAssetResponseDto::fromStockAsset)
                .collect(Collectors.toList());
    }

    /**
     * Gets a stock asset of the user.
     *
     * @param userEmail The email of the user.
     * @param assetId The ID of the asset to get.
     * @return A list of StockAssetResponseDto objects.
     */
    public Optional<StockAssetResponseDto> getStockAsset(String userEmail, UUID assetId) {
        return stockAssetRepository.findByUserEmailAndId(userEmail, assetId)
                .map(StockAssetResponseDto::fromStockAsset);
    }


    /**
     * Adds a new stock asset to the portfolio.
     *
     * @param userEmail The email of the user.
     * @param addStockAssetDto The DTO containing the stock asset details.
     * @return An Optional containing the created StockAssetResponseDto, or empty if the stock asset is invalid.
     * @throws IOException If an error occurs while validating the stock asset.
     */
    public Optional<StockAssetResponseDto> addStockAsset(String userEmail, AddStockAssetDto addStockAssetDto) throws IOException {
        if (!validator.validate(addStockAssetDto).isEmpty()) {
            return Optional.empty();
        }

        if (userEmail == null || !userEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return Optional.empty();
        }

        if (!isValidStockAsset(addStockAssetDto.getSymbol())) {
            return Optional.empty();
        }


        StockAsset stockAsset = createStockAsset(userEmail, addStockAssetDto);
        stockAssetRepository.save(stockAsset);

        sendAssetUpdateRequest(addStockAssetDto.getSymbol());

        return Optional.of(StockAssetResponseDto.fromStockAsset(stockAsset));
    }

    /**
     * Updates an existing stock asset in the portfolio.
     *
     * @param userEmail The email of the user.
     * @param assetId The ID of the asset to update.
     * @param updateStockAssetDto The DTO containing the updated stock asset details.
     * @return An Optional containing the updated StockAssetResponseDto, or empty if the stock asset is invalid.
     */
    public Optional<StockAssetResponseDto> updateStockAsset(String userEmail, UUID assetId, UpdateStockAssetDto updateStockAssetDto) {
        Optional<StockAsset> optionalStockAsset = stockAssetRepository.findByUserEmailAndId(userEmail, assetId);

        if (!validator.validate(updateStockAssetDto).isEmpty()) {
            return Optional.empty();
        }

        if (optionalStockAsset.isEmpty()) {
            return Optional.empty();
        }

        StockAsset stockAsset = optionalStockAsset.get();
        stockAsset.setQuantity(updateStockAssetDto.getQuantity());
        stockAsset.setPrice(updateStockAssetDto.getPrice());
        stockAsset.setPurchaseTimestamp(updateStockAssetDto.getPurchaseTimestamp());

        stockAssetRepository.save(stockAsset);

        return Optional.of(StockAssetResponseDto.fromStockAsset(stockAsset));
    }

    /**
     * Deletes a stock asset from the portfolio.
     *
     * @param userEmail The email of the user.
     * @param assetId The ID of the asset to delete.
     * @return true if the stock asset was deleted, false otherwise.
     */
    public boolean deleteStockAsset(String userEmail, UUID assetId) {
        Optional<StockAsset> optionalStockAsset = stockAssetRepository.findByUserEmailAndId(userEmail, assetId);

        if (optionalStockAsset.isEmpty()) {
            return false;
        }

        stockAssetRepository.delete(optionalStockAsset.get());

        return true;
    }

    /**
     * Validates a stock asset.
     *
     * @param symbol The symbol of the stock asset.
     * @return true if the stock asset is valid, false otherwise.
     * @throws IOException If an error occurs while validating the stock asset.
     */
    private boolean isValidStockAsset(String symbol) throws IOException {
        return iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()
                .stream()
                .anyMatch(iexSymbol -> iexSymbol.symbol.equals(symbol));
    }

    /**
     * Creates a new stock asset.
     *
     * @param userEmail The email of the user.
     * @param addStockAssetDto The DTO containing the stock asset details.
     * @return The created StockAsset.
     */
    private StockAsset createStockAsset(String userEmail, AddStockAssetDto addStockAssetDto) {
        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol(addStockAssetDto.getSymbol());
        stockAsset.setQuantity(addStockAssetDto.getQuantity());
        stockAsset.setPrice(addStockAssetDto.getPrice());
        stockAsset.setPurchaseTimestamp(addStockAssetDto.getPurchaseTimestamp());
        return stockAsset;
    }

    /**
     * Sends an asset update request.
     *
     * @param symbol The symbol of the stock asset.
     */
    private void sendAssetUpdateRequest(String symbol) {
        AssetUpdateRequestEvent assetUpdateRequestEvent = new AssetUpdateRequestEvent();
        assetUpdateRequestEvent.setSymbol(symbol);
        assetUpdateRequestEvent.setAssetType(AssetUpdateRequestEvent.AssetType.STOCK);
        assetUpdateRequestTemplate.send("asset-update-request-topic", assetUpdateRequestEvent);
    }
}