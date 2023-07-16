package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Item {
    @EqualsAndHashCode.Exclude
    @Positive
    private long id;
    @NotNull
    private User owner;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @AssertTrue
    private Boolean available;
}
