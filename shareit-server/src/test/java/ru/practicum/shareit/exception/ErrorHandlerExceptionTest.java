package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.exception.BookingAlreadyProcessedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        // Этот метод должен быть в вашем ErrorHandlerException
        // Если его нет, добавьте его или используйте существующий метод
        ResponseEntity<ErrorResponseException> response = errorHandler.handleBadRequestExceptions(
                new BadRequestException("Parameter 'param' should be of type Long"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getError().contains("param"));
    }
}