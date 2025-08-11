package ru.practicum.shareit.booking.exception;

public class BookingAlreadyProcessedException extends RuntimeException {

    public BookingAlreadyProcessedException(String message) {
        super(message);
    }
}