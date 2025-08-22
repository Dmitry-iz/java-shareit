package ru.practicum.shareit.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestGatewayConfig {

    @Bean
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }
}
