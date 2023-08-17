package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentRequestDtoGateway {
    private Long authorId;
    private Long itemId;

    @NotBlank(message = "Комментарий невозможно создать без описания")
    private String text;
}
