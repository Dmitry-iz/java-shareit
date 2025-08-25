package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Booking not found";
        BookingNotFoundException exception = new BookingNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithMessageAndSetCauseManually() {
        String message = "Booking not found";
        Throwable cause = new RuntimeException("Root cause");

        BookingNotFoundException exception = new BookingNotFoundException(message);
        exception.initCause(cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}