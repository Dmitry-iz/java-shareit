//package ru.practicum.shareit.exception;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//
//import jakarta.validation.ConstraintViolationException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class GatewayErrorHandlerTest {
//
//    private final GatewayErrorHandler errorHandler = new GatewayErrorHandler();
//
//    @Test
//    void handleConstraintViolation_ShouldReturnBadRequest() {
//        ConstraintViolationException ex = mock(ConstraintViolationException.class);
//        when(ex.getConstraintViolations()).thenReturn(java.util.Set.of());
//
//        ResponseEntity<GatewayErrorHandler.ErrorResponse> response = errorHandler.handleConstraintViolation(ex);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
//        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
//        when(ex.getName()).thenReturn("param");
//        when(ex.getRequiredType()).thenReturn((Class) Long.class);
//
//        ResponseEntity<GatewayErrorHandler.ErrorResponse> response = errorHandler.handleTypeMismatch(ex);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertTrue(response.getBody().getError().contains("param"));
//    }
//
//    @Test
//    void handleIllegalArgument_ShouldReturnBadRequest() {
//        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
//
//        ResponseEntity<GatewayErrorHandler.ErrorResponse> response = errorHandler.handleIllegalArgument(ex);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Invalid argument", response.getBody().getError());
//    }
//
//    @Test
//    void handleMethodArgumentNotValid_ShouldReturnBadRequest() {
//        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
//        when(ex.getBindingResult()).thenReturn(null);
//
//        ResponseEntity<GatewayErrorHandler.ErrorResponse> response = errorHandler.handleValidationExceptions(ex);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void handleHttpClientError_ShouldReturnSameStatus() {
//        org.springframework.web.client.HttpClientErrorException ex =
//                org.springframework.web.client.HttpClientErrorException.create(
//                        HttpStatus.BAD_REQUEST, "Bad Request", null, "error".getBytes(), null);
//
//        ResponseEntity<Object> response = errorHandler.handleHttpClientError(ex);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void handleHttpServerError_ShouldReturnSameStatus() {
//        org.springframework.web.client.HttpServerErrorException ex =
//                org.springframework.web.client.HttpServerErrorException.create(
//                        HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null, "error".getBytes(), null);
//
//        ResponseEntity<Object> response = errorHandler.handleHttpServerError(ex);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Test
//    void handleGenericException_ShouldReturnInternalError() {
//        Exception ex = new Exception("Unexpected error");
//
//        ResponseEntity<GatewayErrorHandler.ErrorResponse> response = errorHandler.handleGenericException(ex);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertTrue(response.getBody().getError().contains("Unexpected error"));
//    }
//
//    @Test
//    void errorResponse_ShouldContainErrorMessage() {
//        GatewayErrorHandler.ErrorResponse errorResponse = new GatewayErrorHandler.ErrorResponse("Test error");
//
//        assertEquals("Test error", errorResponse.getError());
//    }
//}
//

