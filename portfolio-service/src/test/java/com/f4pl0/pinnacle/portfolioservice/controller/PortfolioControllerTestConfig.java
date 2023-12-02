package com.f4pl0.pinnacle.portfolioservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.f4pl0.IEXCloudClient;
import io.github.f4pl0.reference.Reference;
import io.github.f4pl0.reference.data.IEXTradingSymbol;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
public class PortfolioControllerTestConfig {

    @Bean
    @Primary
    public IEXCloudClient mockIEXCloudClient() throws IOException {
        IEXCloudClient mockIEXCloudClient = mock(IEXCloudClient.class);
        when(mockIEXCloudClient.fetchReferenceData()).thenReturn(mock(Reference.class));

        IEXTradingSymbol iexTradingSymbol1 = new IEXTradingSymbol();
        iexTradingSymbol1.symbol = "AAPL";
        iexTradingSymbol1.name = "Apple Inc.";

        IEXTradingSymbol iexTradingSymbol2 = new IEXTradingSymbol();
        iexTradingSymbol2.symbol = "GOOG";
        iexTradingSymbol2.name = "Google Inc.";

        when(mockIEXCloudClient.fetchReferenceData().dailyIEXTradingSymbols()).thenReturn(
                new ArrayList<>() {{
                    add(iexTradingSymbol1);
                    add(iexTradingSymbol2);
                }});
        return mockIEXCloudClient;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
