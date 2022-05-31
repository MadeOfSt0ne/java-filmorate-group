package ru.yandex.practicum.filmorate.validator;

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
     */
    public static User validate(User user) {
        if (user.getId() == null) {
            user = user.toBuilder().id(IdProvider.getNextLongId(User.class)).build();
        }

        if (user.getName() == null || user.getName().equals("")) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        return user;
    }
}
