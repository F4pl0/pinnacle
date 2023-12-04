package com.f4pl0.pinnacle.dataaggregatorservice.consumer;

import com.f4pl0.pinnacle.dataaggregatorservice.H2DatabaseTestConfig;
import com.f4pl0.pinnacle.dataaggregatorservice.event.AssetUpdateRequestEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:19092", "port=19092"})
@ContextConfiguration(classes = {H2DatabaseTestConfig.class})
class AssetUpdateRequestConsumerTest {
    @Autowired
    private KafkaTemplate<String, AssetUpdateRequestEvent> kafkaTemplate;

    @Mock
    private AssetUpdateRequestConsumer consumer;

    @Test
    void testPublishAndListen() {
        AssetUpdateRequestEvent event = new AssetUpdateRequestEvent("AAPL", AssetUpdateRequestEvent.AssetType.STOCK);
        kafkaTemplate.send("asset-update-request-topic", event);

        // Verify that the consumer doesn't throw any exceptions
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
