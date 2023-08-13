package ru.practicum.shariet.item.dto;

import lombok.*;
import ru.practicum.shariet.booking.dto.StatusOfBooking;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingForItemDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private Long bookerId;
    private StatusOfBooking status;
}