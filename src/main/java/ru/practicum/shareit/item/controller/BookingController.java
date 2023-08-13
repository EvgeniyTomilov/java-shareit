package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Utils.SHARER_USER_ID;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto add(@RequestHeader(SHARER_USER_ID) Long bookerId,
                                  @Valid @RequestBody BookingRequestDto bookingDto) {
        log.info("Add new booking: {} - Started", bookingDto);
        BookingResponseDto bookingDtoFromRepo = bookingService.addNewBooking(bookerId, bookingDto);
        log.info("Create booking: {} - Finished", bookingDtoFromRepo);
        return bookingDtoFromRepo;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        log.info("Set status {} for booking id: {} by user id {}  - Started", approved, bookingId, ownerId);
        BookingResponseDto bookingDtoFromRepo = bookingService.approveBooking(ownerId, bookingId, approved);
        log.info("Set status: {} - Finished", bookingDtoFromRepo.getStatus());
        return bookingDtoFromRepo;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Search for booking id {} - Started", bookingId);
        BookingResponseDto bookingResponseDto = bookingService.getBooking(bookingId, userId);
        log.info("Booking {} was found", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestHeader(SHARER_USER_ID) Long bookerId,
                                                @RequestParam(defaultValue = "ALL") State state,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Search user's (id {}) {} bookings - Started", bookerId, state);
        List<BookingResponseDto> bookingsOfUser = bookingService.getBookings(bookerId, state, from, size);
        log.info("{} {} bookings was found", bookingsOfUser.size(), state);
        return bookingsOfUser;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOwner(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") State state,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Search {} bookings of owner's (id {}) items - Started", state, ownerId);
        List<BookingResponseDto> bookingsOfOwnerItems =
                bookingService.getBookingsForOwner(ownerId, state, from, size);
        log.info("{} {} bookings was found", state, bookingsOfOwnerItems.size());
        return bookingsOfOwnerItems;
    }
}