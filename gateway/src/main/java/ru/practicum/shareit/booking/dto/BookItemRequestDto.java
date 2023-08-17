package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    private long itemId;
    @FutureOrPresent
    @NotNull(message = "Время начала бронирования не может быть пустым")
    private LocalDateTime start;
    @NotNull(message = "Время завершения бронирования не может быть пустым")
    @Future
    private LocalDateTime end;
}
