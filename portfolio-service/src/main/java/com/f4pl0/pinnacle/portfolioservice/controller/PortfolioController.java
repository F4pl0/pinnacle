package com.f4pl0.pinnacle.portfolioservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

    @GetMapping("/api/portfolio")
    public Authentication getPortfolio(
            Authentication authentication
    ) {
        return authentication;
    }
}
