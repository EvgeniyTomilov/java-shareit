package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Slf4j
@UtilityClass
public final class BookingMapper {
    public Optional<Booking> requestDtoToEntity(BookingRequestDto dto, Item item, User booker) {
        Booking booking = new Booking();
        if (dto == null) {
            return Optional.empty();
        } else {
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            booking.setItem(item);
            booking.setStatus(dto.getStatus());
            booking.setBooker(booker);
        }
        return Optional.of(booking);
    }

    public Optional<BookingResponseDto> entityToResponseDto(Booking entity) {
        BookingResponseDto dto = new BookingResponseDto();

        dto.setStart(entity.getStart());
        dto.setEnd(entity.getEnd());
        dto.setItem(ItemMapper.makeDtoFromItem(entity.getItem()).get());
        dto.setStatus(entity.getStatus());
        dto.setId(entity.getId());
        dto.setBooker(UserMapper.makeDto(entity.getBooker()).get());

        return Optional.of(dto);
    }

    public Optional<BookingForItemDto> entityToBookingForItemDto(Booking booking) {
        BookingForItemDto dto = new BookingForItemDto();

        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.makeDtoFromItem(booking.getItem()).get());
        dto.setStatus(booking.getStatus());
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return Optional.of(dto);
    }
}