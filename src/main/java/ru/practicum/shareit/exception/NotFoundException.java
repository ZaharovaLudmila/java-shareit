package ru.practicum.shareit.exception;

public class NotFoundException extends IllegalStateException {

    public NotFoundException(String message) {
        super(message);
    }
}
