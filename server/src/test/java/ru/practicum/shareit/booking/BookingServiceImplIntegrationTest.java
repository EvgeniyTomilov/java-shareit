package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingService;
import ru.practicum.shareit.booking.model.StateForBooking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceImplIntegrationTest {

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(2);
    private User userOwner;
    private User userBooker;
    private Item item1;
    private Item item2;
    private BookingRequestDto bookingRequestDto;

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        bookingRequestDto = BookingRequestDto.builder()
                .start(start)
                .end(end)
                .build();

        userOwner = User.builder()
                .email("o@o.ru")
                .name("OwnerUser")
                .build();

        userBooker = User.builder()
                .email("b@b.ru")
                .name("BookerUser")
                .build();

        item1 = Item.builder()
                .description("item description1")
                .isAvailable(true)
                .id(1L)
                .name("test item1")
                .owner(userOwner)
                .build();

        item2 = Item.builder()
                .description("item description2")
                .isAvailable(true)
                .id(2L)
                .name("test item2")
                .owner(userOwner)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
    }

    @Test
    void whenAddNewBooking() {
        userRepository.save(userBooker);
        userRepository.save(userOwner);
        itemRepository.save(item1);
        BookingResponseDto expected = bookingService.addNewBooking(userBooker.getId(), bookingRequestDto);

        BookingResponseDto actual = bookingService.getBooking(expected.getId(), userBooker.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("start", "end").isEqualTo(expected);
    }

    @Test
    void whenApproveBooking() {
        userRepository.save(userBooker);
        userRepository.save(userOwner);
        itemRepository.save(item1);
        BookingResponseDto savedBooking = bookingService.addNewBooking(userBooker.getId(), bookingRequestDto);
        BookingResponseDto expected = bookingService.approveBooking(userOwner.getId(), savedBooking.getId(), true);

        BookingResponseDto actual = bookingService.getBooking(expected.getId(), userOwner.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenGetBookings() {
        userRepository.save(userBooker);
        userRepository.save(userOwner);
        itemRepository.save(item1);
        itemRepository.save(item2);
        BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(2L)
                .status(StatusOfBooking.WAITING)
                .build();
        bookingRequestDto.setStatus(StatusOfBooking.WAITING);
        BookingResponseDto bookingResponseDto1 = bookingService.addNewBooking(userBooker.getId(), bookingRequestDto);
        BookingResponseDto bookingResponseDto2 = bookingService.addNewBooking(userBooker.getId(), bookingRequestDto2);
        List<BookingResponseDto> expected = List.of(bookingResponseDto2, bookingResponseDto1);

        List<BookingResponseDto> actual = bookingService.getBookings(userBooker.getId(), StateForBooking.WAITING, 0, 2);

        assertThat(actual).usingRecursiveComparison().ignoringFields("start", "end").isEqualTo(expected);
    }

    @Test
    void whenGetBookingForOwner() {
        userRepository.save(userBooker);
        userRepository.save(userOwner);
        item1.setOwner(userOwner);
        item2.setOwner(userOwner);
        itemRepository.save(item1);
        itemRepository.save(item2);
        BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(2L)
                .status(StatusOfBooking.WAITING)
                .build();
        bookingRequestDto.setStatus(StatusOfBooking.WAITING);
        BookingResponseDto bookingResponseDto1 = bookingService.addNewBooking(userBooker.getId(), bookingRequestDto);
        BookingResponseDto bookingResponseDto2 = bookingService.addNewBooking(userBooker.getId(), bookingRequestDto2);
        List<BookingResponseDto> expected = List.of(bookingResponseDto2, bookingResponseDto1);

        List<BookingResponseDto> actual = bookingService.getBookingsForOwner(userOwner.getId(), StateForBooking.WAITING, 0, 2);

        assertThat(actual).usingRecursiveComparison().ignoringFields("start", "end").isEqualTo(expected);
    }

}