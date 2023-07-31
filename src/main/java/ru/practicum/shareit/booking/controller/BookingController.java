package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Utils.SHARER_USER_ID;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader(SHARER_USER_ID) Long bookerId,
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
    public BookingResponseDto findBookingById(@RequestHeader(SHARER_USER_ID) Long userId,
                                              @PathVariable Long bookingId) {
        log.info("Search for booking id {} - Started", bookingId);
        BookingResponseDto bookingResponseDto = bookingService.getBooking(bookingId, userId);
        log.info("Booking {} was found", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestHeader(SHARER_USER_ID) Long bookerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Search user's (id {}) {} bookings - Started", bookerId, state);
        List<BookingResponseDto> bookingsOfUser = bookingService.getBookings(bookerId, state);
        log.info("{} {} bookings was found", bookingsOfUser.size(), state);
        return bookingsOfUser;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOfOwnersItems(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Search {} bookings of owner's (id {}) items - Started", state, ownerId);
        List<BookingResponseDto> bookingsOfOwnerItems = bookingService.getListOfBookingsOfOwnersItems(ownerId, state);
        log.info("{} {} bookings was found", state, bookingsOfOwnerItems.size());
        return bookingsOfOwnerItems;
    }
}