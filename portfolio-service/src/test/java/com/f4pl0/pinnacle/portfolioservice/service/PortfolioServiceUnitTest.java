package com.f4pl0.pinnacle.portfolioservice.service;

import com.f4pl0.pinnacle.portfolioservice.dto.AddStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.dto.UpdateStockAssetDto;
import com.f4pl0.pinnacle.portfolioservice.event.AssetUpdateRequestEvent;
import com.f4pl0.pinnacle.portfolioservice.model.StockAsset;
import com.f4pl0.pinnacle.portfolioservice.repository.StockAssetRepository;
import io.github.f4pl0.IEXCloudClient;
import io.github.f4pl0.reference.Reference;
import io.github.f4pl0.reference.data.IEXTradingSymbol;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortfolioServiceUnitTest {

    @Mock
    private StockAssetRepository stockAssetRepository;

    @Mock
    private IEXCloudClient iexCloudClient;

    @Mock
    private KafkaTemplate<String, AssetUpdateRequestEvent> assetUpdateRequestTemplate;

    @Mock
    private Validator validator;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        validator = mock(Validator.class);
        when(validator.validate(any())).thenAnswer(invocation -> {
            Object argument = invocation.getArgument(0);
            Validator realValidator = Validation.buildDefaultValidatorFactory().getValidator();
            return realValidator.validate(argument);
        });

        ReflectionTestUtils.setField(portfolioService, "validator", validator);
    }

    @Test
    public void testAddStockAsset() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
        iexTradingSymbol.symbol = "AAPL";
        iexTradingSymbol.name = "Apple Inc.";

        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<IEXTradingSymbol>() {{
                    add(iexTradingSymbol);
                }});

        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertTrue(result.isPresent());
        verify(stockAssetRepository, times(1)).save(any(StockAsset.class));
        verify(assetUpdateRequestTemplate, times(1)).send(anyString(), any(AssetUpdateRequestEvent.class));
    }

    @Test
    public void testAddStockAssetWithInvalidSymbol() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("INVALID");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));
        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(new ArrayList<>());

        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testAddStockAssetWithZeroQuantity() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(0);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
        iexTradingSymbol.symbol = "AAPL";
        iexTradingSymbol.name = "Apple Inc.";

        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<IEXTradingSymbol>() {{
                    add(iexTradingSymbol);
                }});

        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testAddStockAssetWithZeroPrice() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.ZERO);
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
        iexTradingSymbol.symbol = "AAPL";
        iexTradingSymbol.name = "Apple Inc.";

        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<IEXTradingSymbol>() {{
                    add(iexTradingSymbol);
                }});

        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testAddStockAssetWithFutureTimestamp() throws IOException {
        String userEmail = "test@example.com";
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis() + 100000);

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
        iexTradingSymbol.symbol = "AAPL";
        iexTradingSymbol.name = "Apple Inc.";

        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<IEXTradingSymbol>() {{
                    add(iexTradingSymbol);
                }});

        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testAddStockAssetWithNullUserEmail() throws IOException {
        String userEmail = null;
        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
        addStockAssetDto.setSymbol("AAPL");
        addStockAssetDto.setQuantity(10);
        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        when(iexCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
        iexTradingSymbol.symbol = "AAPL";
        iexTradingSymbol.name = "Apple Inc.";

        when(iexCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<IEXTradingSymbol>() {{
                    add(iexTradingSymbol);
                }});

        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateStockAsset() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();
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

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.of(stockAsset));

        Optional<StockAsset> result = portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertTrue(result.isPresent());
        assertEquals(updateStockAssetDto.getQuantity(), result.get().getQuantity());
        assertEquals(updateStockAssetDto.getPrice(), result.get().getPrice());
        assertEquals(updateStockAssetDto.getPurchaseTimestamp(), result.get().getPurchaseTimestamp());
        verify(stockAssetRepository, times(1)).save(any(StockAsset.class));
    }

    @Test
    public void testUpdateStockAssetWithAssetNotFound() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(20);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(200.0));
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.empty());

        Optional<StockAsset> result = portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateStockAssetWithZeroQuantity() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(0);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(200.0));
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.of(stockAsset));

        Optional<StockAsset> result = portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateStockAssetWithZeroPrice() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(20);
        updateStockAssetDto.setPrice(BigDecimal.ZERO);
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.of(stockAsset));

        Optional<StockAsset> result = portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateStockAssetWithFutureTimestamp() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();
        UpdateStockAssetDto updateStockAssetDto = new UpdateStockAssetDto();
        updateStockAssetDto.setQuantity(20);
        updateStockAssetDto.setPrice(BigDecimal.valueOf(200.0));
        updateStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis() + 100000);

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.of(stockAsset));

        Optional<StockAsset> result = portfolioService.updateStockAsset(userEmail, assetId, updateStockAssetDto);

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteStockAssetWithNullUserEmail() {
        String userEmail = null;
        UUID assetId = UUID.randomUUID();

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }

    @Test
    public void testDeleteStockAsset() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail(userEmail);
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.of(stockAsset));

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertTrue(result);
        verify(stockAssetRepository, times(1)).delete(any(StockAsset.class));
    }

    @Test
    public void testDeleteStockAssetWithAssetNotFound() {
        String userEmail = "test@example.com";
        UUID assetId = UUID.randomUUID();

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.empty());

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }

    @Test
    public void testDeleteStockAssetWithWrongUserEmail() {
        String userEmail = "wrong@example.com";
        UUID assetId = UUID.randomUUID();

        StockAsset stockAsset = new StockAsset();
        stockAsset.setUserEmail("test@example.com");
        stockAsset.setSymbol("AAPL");
        stockAsset.setQuantity(10);
        stockAsset.setPrice(BigDecimal.valueOf(150.0));
        stockAsset.setPurchaseTimestamp(System.currentTimeMillis());

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.empty());

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }

    @Test
    public void testDeleteStockAssetWithInvalidUserEmail() {
        String userEmail = "invalid";
        UUID assetId = UUID.randomUUID();

        when(stockAssetRepository.findByUserEmailAndId(userEmail, assetId)).thenReturn(Optional.empty());

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }

    @Test
    public void testDeleteStockAssetWithNullAssetId() {
        String userEmail = "test@example.com";
        UUID assetId = null;

        boolean result = portfolioService.deleteStockAsset(userEmail, assetId);

        assertFalse(result);
    }
}