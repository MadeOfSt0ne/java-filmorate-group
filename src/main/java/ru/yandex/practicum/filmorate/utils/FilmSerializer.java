package ru.yandex.practicum.filmorate.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.util.ArrayList;

public class FilmSerializer extends StdSerializer<Film> {
    public FilmSerializer() {
        this(null);
    }

    protected FilmSerializer(Class<Film> t) {
        super(t);
    }

    @Override
    public void serialize(Film value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getId());
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("releaseDate", value.getReleaseDate().toString());
        jgen.writeStringField("description", value.getDescription());
        jgen.writeNumberField("duration", value.getDuration());
        jgen.writeObjectField("mpa", value.getMpa());
        jgen.writeObjectField("genres",
                value.getGenres() != null ? new ArrayList<>(value.getGenres()).stream().sorted() : null
        );
        jgen.writeEndObject();
    }
}
