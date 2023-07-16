package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
