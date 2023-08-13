package ru.practicum.shariet.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
