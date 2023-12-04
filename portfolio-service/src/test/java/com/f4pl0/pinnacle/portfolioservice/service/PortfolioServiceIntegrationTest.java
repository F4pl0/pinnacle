package com.f4pl0.pinnacle.portfolioservice.service;

import com.f4pl0.pinnacle.portfolioservice.dto.stock.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.StockAssetResponseDto;
import com.f4pl0.pinnacle.portfolioservice.dto.stock.UpdateStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import com.f4pl0.pinnacle.portfolioservice.repository.StockAssetRepository;
import io.github.f4pl0.IEXCloudClient;
import io.github.f4pl0.reference.Reference;
import io.github.f4pl0.reference.data.IEXTradingSymbol;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
class PortfolioServiceIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockAssetRepository stockAssetRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() throws IOException {
        IEXCloudClient iexCloudClient = mock(IEXCloudClient.class);

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
        iexTradingSymbol.symbol = "AAPL";
        iexTradingSymbol.name = "Apple Inc.";

        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<IEXTradingSymbol>() {{
                    add(iexTradingSymbol);
                }});

        portfolioService = new PortfolioService(
                stockAssetRepository,
                iexCloudClient,
                mock(KafkaTemplate.class),
                Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Test
    void testAddStockAsset() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        Optional<StockAssetResponseDto> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        assertEquals(addStockAssetDto.getSymbol(), result.get().getSymbol());
        assertEquals(addStockAssetDto.getQuantity(), result.get().getQuantity());
        assertEquals(addStockAssetDto.getPrice(), result.get().getPrice());
        assertEquals(addStockAssetDto.getPurchaseTimestamp(), result.get().getPurchaseTimestamp());
    }

    @Test
    void testAddStockAssetWithInvalidUserEmail() throws IOException {
        String userEmail = "invalid";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        Optional<StockAssetResponseDto> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    void testAddStockAssetWithNullSymbol() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol(null);
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        Optional<StockAssetResponseDto> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    void testAddStockAssetWithNegativeQuantity() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(-10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        Optional<StockAssetResponseDto> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateStockAsset() {
        String userEmail = "test@example.com";
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(20);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(200.0));
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        entityManager.persist(stockAsset);

        Optional<StockAssetResponseDto> result =
                portfolioService.updateStockAsset(stockAsset.getUserEmail(), stockAsset.getId(), updateStockAssetDto);

        assertTrue(result.isPresent());
        assertEquals(updateStockAssetDto.getQuantity(), result.get().getQuantity());
        assertEquals(updateStockAssetDto.getPrice(), result.get().getPrice());
        assertEquals(updateStockAssetDto.getPurchaseTimestamp(), result.get().getPurchaseTimestamp());
    }

    @Test
    void testUpdateStockAssetWithInvalidUserEmail() {
        String userEmail = "invalid";
        UUID assetId = UUID.randomUUID();
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(20);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(200.0));
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail("test@example.com");
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        entityManager.persist(stockAsset);

        Optional<StockAssetResponseDto> result =
                portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateStockAssetWithNegativePrice() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(20);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(-200.0));
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        entityManager.persist(stockAsset);

        Optional<StockAssetResponseDto> result =
                portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteStockAsset() {
        String userEmail = "test@example.com";

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        entityManager.persist(stockAsset);

        boolean result = portfolioService.deleteStockAsset(stockAsset.getUserEmail(), stockAsset.getId());

        assertTrue(result);
        assertFalse(stockAssetRepository.findById(stockAsset.getId()).isPresent());
    }

    @Test
    void testDeleteStockAssetWithInvalidUserEmail() {
        String userEmail = "invalid";
        UUID assetId = UUID.randomUUID();

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail("test@example.com");
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        entityManager.persist(stockAsset);

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }

    @Test
    void testDeleteStockAssetWithInvalidAssetId() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }

    @Test
    void testDeleteStockAssetWithNullAssetId() {
        String userEmail = "test@example.com";
        UUID assetId = null;

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }
}