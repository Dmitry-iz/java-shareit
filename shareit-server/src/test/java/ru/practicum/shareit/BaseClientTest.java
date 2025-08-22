//package ru.practicum.shareit.client;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.*;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BaseClientTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    private BaseClient baseClient;
//    private final String serverUrl = "http://localhost:8080";
//
//    @BeforeEach
//    void setUp() {
//        baseClient = new BaseClient(restTemplate, serverUrl);
//    }
//
//    @Test
//    void get_WithoutParameters_ShouldCallMakeAndSendRequest() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = baseClient.get("/test", 1L);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(eq(serverUrl + "/test"), eq(HttpMethod.GET), any(), eq(Object.class));
//    }
//
//    @Test
//    void get_WithParameters_ShouldCallMakeAndSendRequest() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        Map<String, Object> parameters = Map.of("param", "value");
//
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = baseClient.get("/test", 1L, parameters);
//
//        assertEquals(expectedResponse, response);
//    }
//
//    @Test
//    void post_WithBody_ShouldCallMakeAndSendRequest() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        Object requestBody = new Object();
//
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = baseClient.post("/test", 1L, requestBody);
//
//        assertEquals(expectedResponse, response);
//    }
//
//    @Test
//    void patch_WithBody_ShouldCallMakeAndSendRequest() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        Object requestBody = new Object();
//
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = baseClient.patch("/test", 1L, requestBody);
//
//        assertEquals(expectedResponse, response);
//    }
//
//    @Test
//    void delete_ShouldCallMakeAndSendRequest() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
//
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = baseClient.delete("/test", 1L);
//
//        assertEquals(expectedResponse, response);
//    }
//
//    @Test
//    void makeAndSendRequest_WithHttpClientError_ShouldReturnErrorResponse() {
//        HttpClientErrorException exception = HttpClientErrorException.create(
//                HttpStatus.BAD_REQUEST, "Bad Request",
//                new HttpHeaders(), "Error message".getBytes(), null);
//
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenThrow(exception);
//
//        ResponseEntity<Object> response = baseClient.get("/test", 1L);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertArrayEquals("Error message".getBytes(), (byte[]) response.getBody());
//    }
//
//    @Test
//    void makeAndSendRequest_WithHttpServerError_ShouldReturnErrorResponse() {
//        HttpServerErrorException exception = HttpServerErrorException.create(
//                HttpStatus.INTERNAL_SERVER_ERROR, "Server Error",
//                new HttpHeaders(), "Server error".getBytes(), null);
//
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenThrow(exception);
//
//        ResponseEntity<Object> response = baseClient.get("/test", 1L);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Test
//    void makeAndSendRequest_WithGenericException_ShouldReturnInternalError() {
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        ResponseEntity<Object> response = baseClient.get("/test", 1L);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertTrue(response.getBody().toString().contains("Unexpected error"));
//    }
//
//    @Test
//    void defaultHeaders_WithUserId_ShouldSetHeaders() {
//        HttpHeaders headers = baseClient.defaultHeaders(1L);
//
//        assertNotNull(headers);
//        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
//        assertTrue(headers.getAccept().contains(MediaType.APPLICATION_JSON));
//        assertEquals("1", headers.getFirst("X-Sharer-User-Id"));
//    }
//
//    @Test
//    void defaultHeaders_WithoutUserId_ShouldNotSetUserIdHeader() {
//        HttpHeaders headers = baseClient.defaultHeaders(null);
//
//        assertNotNull(headers);
//        assertNull(headers.getFirst("X-Sharer-User-Id"));
//    }
//
//    @Test
//    void prepareGatewayResponse_WithSuccessfulResponse_ShouldReturnResponse() {
//        ResponseEntity<Object> originalResponse = ResponseEntity.ok("success");
//        ResponseEntity<Object> result = BaseClient.prepareGatewayResponse(originalResponse);
//
//        assertEquals(originalResponse, result);
//    }
//
//    @Test
//    void prepareGatewayResponse_WithErrorResponseWithBody_ShouldReturnResponse() {
//        ResponseEntity<Object> originalResponse = ResponseEntity.badRequest().body("error");
//        ResponseEntity<Object> result = BaseClient.prepareGatewayResponse(originalResponse);
//
//        assertEquals(originalResponse, result);
//    }
//
//    @Test
//    void prepareGatewayResponse_WithErrorResponseWithoutBody_ShouldReturnResponse() {
//        ResponseEntity<Object> originalResponse = ResponseEntity.badRequest().build();
//        ResponseEntity<Object> result = BaseClient.prepareGatewayResponse(originalResponse);
//
//        assertEquals(originalResponse, result);
//    }
//}