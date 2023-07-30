package ru.practicum.shareit.comment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {

    private Long id;

    private String text;

    @NotBlank(message = "Имя автора комментария должно быть указано")
    private String authorName;
    private LocalDateTime created;
}
