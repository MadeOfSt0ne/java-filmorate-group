package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение для ошибки при собственной валидации.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String s) {
        super(s);
    }
}
