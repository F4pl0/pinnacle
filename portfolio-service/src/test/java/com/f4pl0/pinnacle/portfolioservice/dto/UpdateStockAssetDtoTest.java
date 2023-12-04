package com.f4pl0.pinnacle.portfolioservice.dto;

import com.f4pl0.pinnacle.portfolioservice.dto.stock.UpdateStockAssetDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateStockAssetDtoTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDtoTest() {
        UpdateStockAssetDto dto = new UpdateStockAssetDto();
        dto.setQuantity(10);
        dto.setPrice(new BigDecimal("150.00"));
        dto.setPurchaseTimestamp(Instant.now().toEpochMilli());

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void invalidDtoTest() {
        UpdateStockAssetDto dto = new UpdateStockAssetDto();
        dto.setQuantity(0);
        dto.setPrice(new BigDecimal("-150.00"));
        dto.setPurchaseTimestamp(Instant.now().plusSeconds(10).toEpochMilli());

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void invalidQuantityTest() {
        UpdateStockAssetDto dto = new UpdateStockAssetDto();
        dto.setQuantity(0);
        dto.setPrice(new BigDecimal("150.00"));
        dto.setPurchaseTimestamp(Instant.now().toEpochMilli());

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void invalidPriceTest() {
        UpdateStockAssetDto dto = new UpdateStockAssetDto();
        dto.setQuantity(10);
        dto.setPrice(new BigDecimal("-150.00"));
        dto.setPurchaseTimestamp(Instant.now().toEpochMilli());

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void invalidPurchaseTimestampTest() {
        UpdateStockAssetDto dto = new UpdateStockAssetDto();
        dto.setQuantity(10);
        dto.setPrice(new BigDecimal("150.00"));
        dto.setPurchaseTimestamp(Instant.now().plusSeconds(10).toEpochMilli());

        assertFalse(validator.validate(dto).isEmpty());
    }
}