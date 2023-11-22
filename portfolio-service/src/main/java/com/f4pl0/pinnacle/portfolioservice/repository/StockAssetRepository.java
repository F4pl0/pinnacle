package com.f4pl0.pinnacle.portfolioservice.repository;

import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockAssetRepository extends JpaRepository<StockAsset, UUID> {
}
