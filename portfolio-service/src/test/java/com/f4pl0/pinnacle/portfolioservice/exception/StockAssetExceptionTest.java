package com.f4pl0.pinnacle.portfolioservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StockAssetExceptionTest {

    @Test
    public void stockAssetExceptionMessageTest() {
        String expectedMessage = "StockAssetException occurred";
        StockAssetException exception = new StockAssetException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}