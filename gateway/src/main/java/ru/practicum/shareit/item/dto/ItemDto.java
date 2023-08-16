package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    private List<CommentDto> comments;

    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(name, itemDto.name) && Objects.equals(description, itemDto.description) && Objects.equals(ownerId, itemDto.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, ownerId);
    }
}

