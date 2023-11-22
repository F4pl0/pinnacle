package com.f4pl0.pinnacle.portfolioservice.controller;

import com.f4pl0.pinnacle.portfolioservice.dto.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.exception.StockAssetException;
import com.f4pl0.pinnacle.portfolioservice.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping("/")
    public Authentication getPortfolio(
            Authentication authentication
    ) {
        return authentication;
    }

    @PostMapping("/api/portfolio/asset/stock")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPortfolioAsset(
            Authentication authentication,
            @Valid @RequestBody AddStockAssetDto body
    ) {
        portfolioService.addStockAsset(authentication.getName(), body);
    }

    @ExceptionHandler({ StockAssetException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException() {}
}
