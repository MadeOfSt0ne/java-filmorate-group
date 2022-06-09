package ru.yandex.practicum.filmorate.events;

import org.springframework.context.ApplicationEvent;

public class FilmLikeAddedEvent extends ApplicationEvent {
    public FilmLikeAddedEvent(Object source) {
        super(source);
    }
}
