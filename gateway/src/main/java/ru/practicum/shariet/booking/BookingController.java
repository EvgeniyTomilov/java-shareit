package ru.practicum.shariet.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shariet.booking.dto.BookItemRequestDto;
import ru.practicum.shariet.booking.dto.BookingState;
import ru.practicum.shariet.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shariet.util.Utils.SHARER_USER_ID;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(SHARER_USER_ID) long userId,
                                              @RequestParam(defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestHeader(SHARER_USER_ID) long ownerId,
                                                      @RequestParam(defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsForOwner(ownerId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(SHARER_USER_ID) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("GATEWAY: Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("GATEWAY: Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@Positive @RequestHeader(SHARER_USER_ID) Long ownerId,
                                          @Positive @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        if (approved == null) {
            log.warn("статус подтверждения не может быть пустым");
            throw new ValidationException("Approve validation error. Status is null");
        }
        log.info("GATEWAY: Set status {} for booking id: {} by user id {}  - Started", approved, bookingId, ownerId);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

}