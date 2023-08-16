package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoGateway {

    private Long id;
    private String text;
    @NotBlank(message = "Имя автора комментария должно быть указано")
    private String authorName;
    private LocalDateTime created;
}
