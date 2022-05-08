package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class User {
    Long id;

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

    Set<Long> friends = new HashSet<>();
}
