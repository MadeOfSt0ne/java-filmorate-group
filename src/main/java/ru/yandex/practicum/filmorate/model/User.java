package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.yandex.practicum.filmorate.utils.IdProvider;

import javax.validation.constraints.*;
import java.util.Date;

@Value
public class User {
    long id = IdProvider.getNextId(User.class);

    @Email
    @NotBlank
    String email;

    @Pattern(regexp = "^\\w+$")
    @NotBlank
    String login;

    String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Past
    Date birthday;
}
