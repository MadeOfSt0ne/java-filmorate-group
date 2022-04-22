package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение для ошибки при самостоятельной валидации.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String s) {
        super(s);
    }
}
