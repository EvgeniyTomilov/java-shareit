package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateForBooking;


import java.util.List;

public interface BookingMapperService {
    Booking addStatusToBooking(Long ownerId, Long bookingId, Boolean approved);

    Booking bookingRequestPrepareForAdd(Long bookerId, BookingRequestDto dto);

    void accessVerification(Booking bookingFromRepo, Long userId);

    List<BookingResponseDto> prepareResponseDtoList(Long bookerId, StateForBooking state,
                                                    Integer from, Integer size);

    List<BookingResponseDto> prepareResponseDtoListForOwner(Long ownerId, StateForBooking state, Integer from, Integer size);

}
