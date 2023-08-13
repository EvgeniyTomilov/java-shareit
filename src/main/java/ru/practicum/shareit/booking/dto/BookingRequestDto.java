package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingRequestDto {

    @NotNull(message = "Время начала бронирования не может быть пустым")
    private LocalDateTime start;

    @NotNull(message = "Время завершения бронирования не может быть пустым")
    private LocalDateTime end;

    @NotNull(message = "Бронирование невозможно без указания Item Id")
    private Long itemId;

    private StatusOfBooking status;
}