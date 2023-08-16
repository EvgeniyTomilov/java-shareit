package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingService;
import ru.practicum.shareit.booking.model.StateForBooking;

import java.util.List;

import static ru.practicum.shareit.utils.HeaderUserIdConst.HEADER_USER_ID;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto add(@RequestHeader(HEADER_USER_ID) Long bookerId,
                                  @RequestBody BookingRequestDto bookingDto) {
        log.info("SERVER: Add new booking: {} - Started", bookingDto);
        BookingResponseDto bookingDtoFromRepo = bookingService.addNewBooking(bookerId, bookingDto);
        log.info("SERVER: Create booking: {} - Finished", bookingDtoFromRepo);
        return bookingDtoFromRepo;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(HEADER_USER_ID) Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        log.info("SERVER: Set status {} for booking id: {} by user id {}  - Started", approved, bookingId, ownerId);
        BookingResponseDto bookingDtoFromRepo = bookingService.approveBooking(ownerId, bookingId, approved);
        log.info("SERVER: Set status: {} - Finished", bookingDtoFromRepo.getStatus());
        return bookingDtoFromRepo;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @PathVariable Long bookingId) {
        log.info("SERVER: Search for booking id {} - Started", bookingId);
        BookingResponseDto bookingResponseDto = bookingService.getBooking(bookingId, userId);
        log.info("SERVER: Booking {} was found", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestHeader(HEADER_USER_ID) Long bookerId,
                                                @RequestParam(defaultValue = "ALL") StateForBooking state,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.info("SERVER: Search user's (id {}) {} bookings - Started", bookerId, state);
        List<BookingResponseDto> bookingsOfUser = bookingService.getBookings(bookerId, state, from, size);
        log.info("SERVER: {} {} bookings was found", bookingsOfUser.size(), state);
        return bookingsOfUser;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOwner(@RequestHeader(HEADER_USER_ID) Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") StateForBooking state,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.info("SERVER: Search {} bookings of owner's (id {}) items - Started", state, ownerId);
        List<BookingResponseDto> bookingsOfOwnerItems =
                bookingService.getBookingsForOwner(ownerId, state, from, size);
        log.info("SERVER: {} {} bookings was found", state, bookingsOfOwnerItems.size());
        return bookingsOfOwnerItems;
    }
}