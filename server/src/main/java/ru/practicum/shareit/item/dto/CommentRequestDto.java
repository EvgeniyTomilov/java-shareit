package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentRequestDto {
    private Long authorId;
    private Long itemId;
    private String text;
}
