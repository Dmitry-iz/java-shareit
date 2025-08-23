package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.client.ItemClient;

import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.client.UserClient;


@Configuration
public class ClientConfig {

    @Value("${shareit.server.url:http://localhost:9090}")
    private String serverUrl;

    @Bean
    public BookingClient bookingClient(RestTemplate restTemplate) {
        return new BookingClient(serverUrl, restTemplate);
    }

    @Bean
    public ItemClient itemClient(RestTemplate restTemplate) {
        return new ItemClient(serverUrl, restTemplate);
    }

    @Bean
    public UserClient userClient(RestTemplate restTemplate) {
        return new UserClient(serverUrl, restTemplate);
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplate restTemplate) {
        return new ItemRequestClient(serverUrl, restTemplate);
    }
}