package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.BaseClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TestableBaseClient baseClient;

    // Тестовый класс, который наследуется от BaseClient
    private static class TestableBaseClient extends BaseClient {
        public TestableBaseClient(RestTemplate rest, String serverUrl) {
            super(rest, serverUrl);
        }

        // Публичные методы для тестирования protected методов
        public ResponseEntity<Object> testGet(String path, Long userId) {
            return get(path, userId);
        }

        public ResponseEntity<Object> testGet(String path, Long userId, java.util.Map<String, Object> parameters) {
            return get(path, userId, parameters);
        }

        public ResponseEntity<Object> testPost(String path, Long userId, Object body) {
            return post(path, userId, body);
        }

        public ResponseEntity<Object> testPost(String path, Long userId, java.util.Map<String, Object> parameters, Object body) {
            return post(path, userId, parameters, body);
        }
    }

    @Test
    void get_ShouldReturnResponse_WhenRequestIsSuccessful() {
        // Arrange
        String expectedResponse = "test response";
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(responseEntity);

        // Act
        ResponseEntity<Object> result = baseClient.testGet("/test", 1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
    }

    @Test
    void get_ShouldHandleHttpError_WhenRequestFails() {
        // Arrange
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request",
                new HttpHeaders(), "Error".getBytes(), null);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        // Act
        ResponseEntity<Object> result = baseClient.testGet("/test", 1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void get_WithParameters_ShouldReturnResponse() {
        // Arrange
        String expectedResponse = "test response";
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        java.util.Map<String, Object> parameters = java.util.Map.of("param", "value");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(responseEntity);

        // Act
        ResponseEntity<Object> result = baseClient.testGet("/test", 1L, parameters);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
    }

    @Test
    void post_ShouldReturnResponse_WhenRequestIsSuccessful() {
        // Arrange
        String expectedResponse = "test response";
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        Object requestBody = new Object();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(responseEntity);

        // Act
        ResponseEntity<Object> result = baseClient.testPost("/test", 1L, requestBody);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
    }
}