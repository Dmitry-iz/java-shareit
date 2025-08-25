package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.exception.BookingAlreadyProcessedException;
import ru.practicum.shareit.booking.exception.BookingOwnItemException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerExceptionTest {

    private final ErrorHandlerException errorHandler = new ErrorHandlerException();

    @Test
    void handleUserAlreadyExists_ShouldReturnConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User exists");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleUserAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User exists", response.getBody().getError());
    }

    @Test
    void handleNotFoundExceptions_ShouldReturnNotFound() {
        UserNotFoundException ex = new UserNotFoundException("Not found");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleNotFoundExceptions(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getError());
    }

    @Test
    void handleItemNotFoundException_ShouldReturnNotFound() {
        ItemNotFoundException ex = new ItemNotFoundException("Item not found");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleNotFoundExceptions(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Item not found", response.getBody().getError());
    }

    @Test
    void handleForbiddenExceptions_ShouldReturnForbidden() {
        ItemNotOwnedByUserException ex = new ItemNotOwnedByUserException("Forbidden");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleForbiddenExceptions(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Forbidden", response.getBody().getError());
    }

    @Test
    void handleBookingAccessDeniedException_ShouldReturnForbidden() {
        BookingAccessDeniedException ex = new BookingAccessDeniedException("Access denied");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleForbiddenExceptions(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getError());
    }

    @Test
    void handleConflictExceptions_ShouldReturnConflict() {
        BookingAlreadyProcessedException ex = new BookingAlreadyProcessedException("Conflict");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleConflictExceptions(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody().getError());
    }

    @Test
    void handleBadRequestExceptions_ShouldReturnBadRequest() {
        BadRequestException ex = new BadRequestException("Bad request");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleBadRequestExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().getError());
    }

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("param");
        when(ex.getRequiredType()).thenReturn((Class) Long.class);

        ResponseEntity<ErrorResponseException> response = errorHandler.handleBadRequestExceptions(
                new BadRequestException("Parameter 'param' should be of type Long"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getError().contains("param"));
    }

    @Test
    void handleValidationExceptions_WithMultipleFieldErrors_ShouldReturnFormattedMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name is required");
        FieldError fieldError2 = new FieldError("object", "email", "Email is invalid");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponseException> response = errorHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getError().contains("name: Name is required"));
        assertTrue(response.getBody().getError().contains("email: Email is invalid"));
        assertTrue(response.getBody().getError().contains("; "));
    }

    @Test
    void handleValidationExceptions_WithSingleFieldError_ShouldReturnSingleMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "name", "Name is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponseException> response = errorHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("name: Name is required", response.getBody().getError());
    }

    @Test
    void handleValidationExceptions_WithNoFieldErrors_ShouldReturnEmptyMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponseException> response = errorHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody().getError());
    }

    @Test
    void handleBookingOwnItemException_ShouldReturnForbidden() {
        BookingOwnItemException ex = new BookingOwnItemException("Cannot book own item");

        ResponseEntity<ErrorResponseException> response = errorHandler.handleForbiddenExceptions(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Cannot book own item", response.getBody().getError());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
    }

    @Test
    void errorResponseException_ShouldHaveCorrectStructure() {
        ErrorResponseException errorResponse = new ErrorResponseException("Test error");

        assertEquals("Test error", errorResponse.getError());
        assertNotNull(errorResponse.toString());
    }

    @Test
    void handleMultipleNotFoundExceptions_ShouldAllReturnNotFound() {
        UserNotFoundException userEx = new UserNotFoundException("User not found");
        ItemNotFoundException itemEx = new ItemNotFoundException("Item not found");

        ResponseEntity<ErrorResponseException> userResponse = errorHandler.handleNotFoundExceptions(userEx);
        ResponseEntity<ErrorResponseException> itemResponse = errorHandler.handleNotFoundExceptions(itemEx);

        assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, itemResponse.getStatusCode());
    }
}