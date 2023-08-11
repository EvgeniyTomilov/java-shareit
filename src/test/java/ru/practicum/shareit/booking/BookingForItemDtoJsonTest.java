package ru.practicum.shareit.booking;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@JsonTest
public class BookingForItemDtoJsonTest {

    @Autowired
    private JacksonTester<BookingForItemDto> jsonBookingForItemDto;

    @Test
    @SneakyThrows
    void bookingForItemDtoTest() {
        BookingForItemDto bookingForItemDto = BookingForItemDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(StatusOfBooking.WAITING)
                .build();

        JsonContent<BookingForItemDto> result = jsonBookingForItemDto.write(bookingForItemDto);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }

}
