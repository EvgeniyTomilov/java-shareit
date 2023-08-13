package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingResponseDto addNewBooking(Long bookerId, BookingRequestDto dto);

    BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookings(Long bookerId, State state, Integer from, Integer size);

    List<BookingResponseDto> getBookingsForOwner(Long ownerId, State state, int from, int size);
}