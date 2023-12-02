package com.f4pl0.pinnacle.portfolioservice.repository;

import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockAssetRepository extends JpaRepository<StockAsset, UUID> {
    Optional<StockAsset> findByUserEmailAndId(String userEmail, UUID id);
    List<StockAsset> findByUserEmail(String userEmail);
}
