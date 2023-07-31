package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapperService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service("BookingService")
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepo;
    private BookingMapperService bookingMapperService;

    @Override
    public BookingResponseDto addNewBooking(Long bookerId, BookingRequestDto dto) {
        Booking bookingForSave = bookingMapperService.bookingRequestPrepareForAdd(bookerId, dto);
        Booking newBooking = bookingRepo.save(bookingForSave);
        return BookingMapper.entityToResponseDto(newBooking)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking bookingWithStatus = bookingMapperService.addStatusToBooking(ownerId, bookingId, approved);
        Booking updateBooking = bookingRepo.save(bookingWithStatus);
        return BookingMapper.entityToResponseDto(updateBooking)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking bookingFromRepo = bookingRepo.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование id " + bookingId + " не найдено"));
        bookingMapperService.accessVerification(bookingFromRepo, userId);

        return BookingMapper.entityToResponseDto(bookingFromRepo)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public List<BookingResponseDto> getBookings(Long bookerId, String state) {
        return bookingMapperService.prepareResponseDtoList(bookerId, state);
    }

    @Override
    public List<BookingResponseDto> getListOfBookingsOfOwnersItems(Long ownerId, String state) {
        return bookingMapperService.prepareResponseDtoListForOwner(ownerId, state);
    }
}
