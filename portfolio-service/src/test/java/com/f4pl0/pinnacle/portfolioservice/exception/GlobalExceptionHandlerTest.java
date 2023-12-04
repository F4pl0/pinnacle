package com.f4pl0.pinnacle.portfolioservice.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleStockAssetExceptionTest() {
        StockAssetException exception = mock(StockAssetException.class);
        when(exception.getMessage()).thenReturn("StockAssetException occurred");

        Map<String, String> result = handler.handleStockAssetException(exception);

        assertEquals("StockAssetException occurred", result.get("error"));
    }

    @Test
    void handleIOExceptionTest() {
        Exception exception = mock(Exception.class);
        when(exception.getMessage()).thenReturn("Exception occurred");

        Map<String, String> result = handler.handleIOException(exception);

        assertEquals("Oopsie, looks like server is unhappy at the moment. Please try again later.", result.get("error"));
    }
}