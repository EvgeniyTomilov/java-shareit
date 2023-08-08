package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {

    private Long id;

    private String text;

    @NotBlank(message = "Имя автора комментария должно быть указано")
    private String authorName;
    private LocalDateTime created;

    private Item item;
}
