package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {


    @Test
    void requestDtoToEntity() {
        User user = new User(1L, "Name", "a@a.a");
        Item item = new Item(1L, user, "Item", "Description", true, new ItemRequest());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking expectedBooking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(StatusOfBooking.WAITING)
                .start(start)
                .end(end)
                .build();

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .status(StatusOfBooking.WAITING)
                .build();

        Booking actual = BookingMapper.requestDtoToEntity(bookingRequestDto, item, user).get();
        actual.setId(1L);

        assertEquals(expectedBooking, actual);
    }

    @Test
    void entityToResponseDto() {
        User user = new User(1L, "Name", "a@a.a");
        Item item = new Item(1L, user, "Item", "Description", true, new ItemRequest());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(StatusOfBooking.WAITING)
                .start(start)
                .end(end)
                .build();

        BookingResponseDto expectedDto = new BookingResponseDto(
                1L,
                start,
                end,
                ItemMapper.makeDtoFromItem(item).get(),
                UserMapper.makeDto(user).get(),
                StatusOfBooking.WAITING);

        BookingResponseDto actual = BookingMapper.entityToResponseDto(booking).get();

        assertEquals(expectedDto, actual);
    }

    @Test
    void entityToBookingForItemDto() {
        User user = new User(1L, "Name", "a@a.a");
        Item item = new Item(1L, user, "Item", "Description", true, new ItemRequest());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(StatusOfBooking.WAITING)
                .start(start)
                .end(end)
                .build();

        BookingForItemDto expectedDto = BookingForItemDto.builder()
                .start(start)
                .end(end)
                .item(ItemMapper.makeDtoFromItem(item).get())
                .status(StatusOfBooking.WAITING)
                .id(1L)
                .bookerId(1L)
                .build();

        Optional<BookingForItemDto> actual = BookingMapper.entityToBookingForItemDto(booking);

        assertEquals(expectedDto, actual.get());
    }
}