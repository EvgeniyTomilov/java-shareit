package ru.practicum.shariet.item.dto;

import lombok.*;

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
    }