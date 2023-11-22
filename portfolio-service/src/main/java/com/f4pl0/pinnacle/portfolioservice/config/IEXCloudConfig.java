package com.f4pl0.pinnacle.portfolioservice.config;

import io.github.f4pl0.IEXCloudClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IEXCloudConfig {

    @Bean
    public IEXCloudClient iexCloudClient() {
        return new IEXCloudClient.IEXCloudClientBuilder()
                .setPublishableToken("pk_f6b79eabb7904e709d59fe7b0d7e2d94")
                .build();
    }
}
