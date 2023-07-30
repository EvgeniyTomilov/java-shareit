package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@ToString
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;

    private List<CommentDto> comments;

    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
}
