package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto addNewBooking(Long bookerId, BookingRequestDto dto);

    BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookings(Long userId, StateForBooking state, Integer from, Integer size);

    List<BookingResponseDto> getBookingsForOwner(Long ownerId, StateForBooking status, int from, int size);
}
