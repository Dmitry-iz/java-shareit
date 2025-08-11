package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponseException {
    private final String error;

    public ErrorResponseException(String error) {
        this.error = error;
    }
}


