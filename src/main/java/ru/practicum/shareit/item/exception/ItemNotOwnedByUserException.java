package ru.practicum.shareit.item.exception;

public class ItemNotOwnedByUserException extends RuntimeException {
    public ItemNotOwnedByUserException(String message) {
        super(message);
    }
}
