package com.f4pl0.pinnacle.portfolioservice;

import com.f4pl0.pinnacle.portfolioservice.event.AssetUpdateRequestEvent;
import com.f4pl0.pinnacle.portfolioservice.repository.StockAssetRepository;
import com.f4pl0.pinnacle.portfolioservice.service.PortfolioService;
import io.github.f4pl0.IEXCloudClient;
import io.github.f4pl0.reference.Reference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceUnitTest {
    @Mock
    private StockAssetRepository stockAssetRepository;
    @Mock
    private Reference reference;
    @Mock
    private IEXCloudClient iexCloudClient;
    @Mock
    private KafkaTemplate<String, AssetUpdateRequestEvent> assetUpdateRequestTemplate;
    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void addStockAssetTest() throws IOException {
//        String userEmail = "test@example.com";
//        AddStockAssetDto addStockAssetDto = new AddStockAssetDto();
//        addStockAssetDto.setSymbol("AAPL");
//        addStockAssetDto.setQuantity(10);
//        addStockAssetDto.setPrice(BigDecimal.valueOf(150.0));
//        addStockAssetDto.setPurchaseTimestamp(System.currentTimeMillis());
//
//        IEXTradingSymbol iexTradingSymbol = new IEXTradingSymbol();
//        iexTradingSymbol.symbol = "AAPL";
//        iexTradingSymbol.name = "Apple Inc.";
//
//        when(iexCloudClient.reference.dailyIEXTradingSymbols()).thenReturn(Collections.singletonList(iexTradingSymbol));
//        when(stockAssetRepository.save(any(StockAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Optional<StockAsset> result = portfolioService.addStockAsset(userEmail, addStockAssetDto);
//
//        verify(stockAssetRepository, times(1)).save(any(StockAsset.class));
//        verify(assetUpdateRequestTemplate, times(1)).send(anyString(), any(AssetUpdateRequestEvent.class));
//
//        assertTrue(result.isPresent());
//        assertEquals(addStockAssetDto.getSymbol(), result.get().getSymbol());
//        assertEquals(addStockAssetDto.getQuantity(), result.get().getQuantity());
//        assertEquals(addStockAssetDto.getPrice(), result.get().getPrice());
//        assertEquals(addStockAssetDto.getPurchaseTimestamp(), result.get().getPurchaseTimestamp());
    }
}
