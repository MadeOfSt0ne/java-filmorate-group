package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.IdProvider;

/**
 * Утилитарный класс-валидатор для пользователей.
 */
public class UserValidator {
    /**
     * Валидирует объект пользователя. Генерирует новый идентификатор если его нет.
     *
     * @param user валид. пользователь
     * @return новый объект пользователя
     * @throws ValidationException - если пользователь не валиден.
     */
    public static User validate(User user) {
        if (user.getId() == null) {
            user = user.toBuilder().id(IdProvider.getNextLongId(User.class)).build();
        }

        if (user.getName() == null) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        if (user.getId() < 0) throw new ValidationException("user id must be greater than zero");

        return user;
    }
}
