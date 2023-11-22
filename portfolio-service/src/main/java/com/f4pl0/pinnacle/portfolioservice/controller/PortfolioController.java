package com.f4pl0.pinnacle.portfolioservice.controller;

import com.f4pl0.pinnacle.portfolioservice.dto.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.dto.UpdateStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.exception.StockAssetException;
import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import com.f4pl0.pinnacle.portfolioservice.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is responsible for handling HTTP requests related to the portfolio.
 * It uses the PortfolioService to perform operations on the portfolio.
 */
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    /**
     * This method handles the HTTP POST request to add a new stock asset to the portfolio.
     *
     * @param authentication The authentication object containing the user's details.
     * @param body The AddStockAssetDto object containing the details of the stock asset to be added.
     * @return A ResponseEntity indicating the result of the operation.
     * @throws IOException If there is an error while checking the validity of the stock asset.
     * @throws StockAssetException If the stock asset is invalid.
     */
    @PostMapping("/asset/stock")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addPortfolioAsset(
            Authentication authentication,
            @Valid @RequestBody AddStockAssetDto body
    ) throws IOException, StockAssetException {
        Optional<StockAsset> optionalStockAsset = portfolioService.addStockAsset(authentication.getName(), body);

        if (optionalStockAsset.isEmpty()) {
            throw new StockAssetException("Invalid stock asset: " + body.getSymbol());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * This method handles the HTTP PUT request to update a stock asset in the portfolio.
     *
     * @param authentication The authentication object containing the user's details.
     * @param assetId The ID of the stock asset to be updated.
     * @param body The UpdateStockAssetDto object containing the new details of the stock asset.
     * @return A ResponseEntity indicating the result of the operation.
     * @throws StockAssetException If the stock asset is not found.
     */
    @PutMapping("/asset/stock/{assetId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePortfolioAsset(
            Authentication authentication,
            @PathVariable("assetId") UUID assetId,
            @Valid @RequestBody UpdateStockAssetDto body
    ) throws StockAssetException {
        Optional<StockAsset> optionalStockAsset = portfolioService.updateStockAsset(authentication.getName(), assetId, body);

        if (optionalStockAsset.isEmpty()) {
            throw new StockAssetException("Invalid asset ID: " + assetId);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * This method handles the HTTP DELETE request to remove a stock asset from the portfolio.
     *
     * @param authentication The authentication object containing the user's details.
     * @param assetId The ID of the stock asset to be removed.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @DeleteMapping("/asset/stock/{assetId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deletePortfolioAsset(
            Authentication authentication,
            @PathVariable("assetId") UUID assetId
    ) {
        boolean isDeleted = portfolioService.deleteStockAsset(authentication.getName(), assetId);

        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
