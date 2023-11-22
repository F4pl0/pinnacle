package com.f4pl0.pinnacle.dataaggregatorservice.consumer;

import com.f4pl0.pinnacle.dataaggregatorservice.event.AssetUpdateRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssetUpdateRequestConsumer {

    @KafkaListener(topics = "asset-update-request-topic")
    public void listen(AssetUpdateRequestEvent event) {
        log.debug("Received AssetUpdateRequestEvent for symbol {}, {}", event.getSymbol(), event.getAssetType());
    }
}
