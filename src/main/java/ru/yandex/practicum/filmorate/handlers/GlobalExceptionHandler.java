package ru.yandex.practicum.filmorate.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.NoSuchElementException;

/**
 * Глобальный обработчик исключений для возвращения "значащих" кодов ошибок вместо 500-ых.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "requested object not found")
    Exception handleNoSuchElementException(final Exception e) {
        return e;
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "validation failed")
    Exception handleValidationException(final Exception e) {
        return e;
    }
}
