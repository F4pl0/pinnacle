package com.f4pl0.pinnacle.portfolioservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic updateCompanyDataRequestTaskTopic() {
        return TopicBuilder.name("asset-update-request-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}