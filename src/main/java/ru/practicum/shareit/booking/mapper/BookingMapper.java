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
    public static Optional<Booking> requestDtoToEntity(BookingRequestDto dto, Item item, User booker) {
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

    public static Optional<Booking> updateEntity(Booking booking, BookingRequestDto dto) {
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());
        }
        if (dto.getStart() != null) {
            booking.setStart(dto.getStart());
        }
        if (dto.getEnd() != null) {
            booking.setEnd(dto.getEnd());
        }
        return Optional.ofNullable(booking);
    }

    public static Optional<BookingResponseDto> entityToResponseDto(Booking entity) {
        BookingResponseDto dto = new BookingResponseDto();
        if (entity == null) {
            return Optional.empty();
        } else {
            dto.setStart(entity.getStart());
            dto.setEnd(entity.getEnd());
            dto.setItem(ItemMapper.makeDtoFromItem(entity.getItem())
                    .orElseThrow(() -> new NullPointerException("dto объект не найден")));
            dto.setStatus(entity.getStatus());
            dto.setId(entity.getId());
            dto.setBooker(UserMapper.makeDto(entity.getBooker())
                    .orElseThrow(() -> new NullPointerException("dto объект не найден")));
        }
        return Optional.of(dto);
    }

    public static Optional<Booking> responseDtoToEntity(BookingResponseDto dto, User owner) {
        Booking booking = new Booking();
        if (dto == null) {
            return Optional.empty();
        } else {
            booking.setId(dto.getId());
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            booking.setItem(ItemMapper.makeItem(dto.getItem(), owner)
                    .orElseThrow(() -> new NullPointerException("объект не найден")));
            booking.setStatus(dto.getStatus());
            booking.setBooker(UserMapper.makeUserWithId(dto.getBooker())
                    .orElseThrow(() -> new NullPointerException("объект не найден")));
        }
        return Optional.of(booking);
    }

    public static Optional<BookingForItemDto> entityToBookingForItemDto(Booking booking) {
        BookingForItemDto dto = new BookingForItemDto();
        if (booking == null) {
            return Optional.empty();
        } else {
            dto.setStart(booking.getStart());
            dto.setEnd(booking.getEnd());
            dto.setItem(ItemMapper.makeDtoFromItem(booking.getItem())
                    .orElseThrow(() -> new NullPointerException("dto объект не найден")));
            dto.setStatus(booking.getStatus());
            dto.setId(booking.getId());
            dto.setBookerId(booking.getBooker().getId());
        }
        return Optional.of(dto);
    }
}
