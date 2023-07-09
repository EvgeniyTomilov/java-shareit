package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private Boolean available;
    @NotBlank
    private User owner;
}
