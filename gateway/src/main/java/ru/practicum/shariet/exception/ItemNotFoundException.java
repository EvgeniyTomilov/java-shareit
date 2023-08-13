package ru.practicum.shariet.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
