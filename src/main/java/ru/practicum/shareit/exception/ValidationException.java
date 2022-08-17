package ru.practicum.shareit.exception;

public class ValidationException extends IllegalStateException {

    public ValidationException(String message) {
        super(message);
    }
}
