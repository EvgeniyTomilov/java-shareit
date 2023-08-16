package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapperService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private final Long id1 = 1L;
    private final Long id2 = 2L;
    private BookingRequestDto newBookingRequestDto;
    private BookingResponseDto bookingResponseDtoFromRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private BookingMapperService bookingMapperService;
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusDays(1);
    private User userOwner;
    private User userBooker;
    private Item item;
    private Booking bookingForSave;
    private Booking newBooking;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        newBookingRequestDto = BookingRequestDto.builder()
                .itemId(id1)
                .start(start)
                .end(end)
                .build();

        userOwner = User.builder()
                .id(id1)
                .email("o@o.ru")
                .name("OwnerUser")
                .build();

        userBooker = User.builder()
                .id(id2)
                .email("b@b.ru")
                .name("BookerUser")
                .build();

        item = Item.builder()
                .description("item description")
                .isAvailable(true)
                .id(id1)
                .name("test item")
                .owner(userOwner)
                .build();

        bookingForSave = BookingMapper.requestDtoToEntity(newBookingRequestDto, item, userOwner).orElseThrow();

        newBooking = Booking.builder()
                .booker(userBooker)
                .id(id1)
                .status(StatusOfBooking.WAITING)
                .start(start)
                .end(end)
                .item(item)
                .build();

        bookingResponseDtoFromRepo = BookingMapper.entityToResponseDto(newBooking).orElseThrow();
    }

    @Test
    void addNewBooking_whenAddNewCorrectBookingDto_thenReturnBookingDto() {
        when(bookingMapperService.bookingRequestPrepareForAdd(id1, newBookingRequestDto)).thenReturn(bookingForSave);
        when(bookingRepo.save(bookingForSave)).thenReturn(newBooking);

        BookingResponseDto actualDto = bookingService.addNewBooking(id1, newBookingRequestDto);
        BookingResponseDto expectedDto = BookingMapper.entityToResponseDto(newBooking).orElseThrow();

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void addNewBooking_whenAddNewBookingDtoWithIncorrectBookerId_thenThrowUserNotFoundException() {
        when(bookingMapperService.bookingRequestPrepareForAdd(999L, newBookingRequestDto))
                .thenThrow(UserNotFoundException.class);
        when(bookingMapperService.bookingRequestPrepareForAdd(-999L, newBookingRequestDto))
                .thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.addNewBooking(999L, newBookingRequestDto));
        assertThrows(UserNotFoundException.class,
                () -> bookingService.addNewBooking(-999L, newBookingRequestDto));

        verify(bookingMapperService).bookingRequestPrepareForAdd(999L, newBookingRequestDto);
        verify(bookingRepo, never()).save(bookingForSave);
    }

    @Test
    void approveBooking_whenCorrectBookingAndTrueIsApprove_thenReturnDtoWithApprovedStatus() {
        Booking bookingWithStatus = newBooking;
        bookingWithStatus.setStatus(StatusOfBooking.APPROVED);
        when(bookingMapperService.addStatusToBooking(id1, id2, Boolean.TRUE)).thenReturn(bookingWithStatus);
        when(bookingRepo.save(bookingWithStatus)).thenReturn(bookingWithStatus);

        BookingResponseDto actualDto = bookingService.approveBooking(id1, id2, Boolean.TRUE);
        BookingResponseDto expectedDto = BookingMapper.entityToResponseDto(bookingWithStatus).orElseThrow();

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void approveBooking_whenCorrectBookingAndFalseIsApprove_thenReturnDtoWithRejectedStatus() {
        Booking bookingWithStatus = newBooking;
        bookingWithStatus.setStatus(StatusOfBooking.REJECTED);
        when(bookingMapperService.addStatusToBooking(id1, id2, Boolean.FALSE)).thenReturn(bookingWithStatus);
        when(bookingRepo.save(bookingWithStatus)).thenReturn(bookingWithStatus);

        BookingResponseDto actualDto = bookingService.approveBooking(id1, id2, Boolean.FALSE);
        BookingResponseDto expectedDto = BookingMapper.entityToResponseDto(bookingWithStatus).orElseThrow();

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void approveBooking_whenCorrectBookingAndNullStatus_thenThrowValidationException() {
        when(bookingMapperService.addStatusToBooking(id1, id2, null))
                .thenThrow(ValidationException.class);
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(id1, id2, null));

        verify(bookingMapperService, times(1))
                .addStatusToBooking(id1, id2, null);
    }


    @Test
    void getBooking_whenRequestCorrect_thenReturnDto() {
        when(bookingRepo.findById(id1)).thenReturn(Optional.ofNullable(newBooking));
        doNothing().when(bookingMapperService).accessVerification(newBooking, id1);

        BookingResponseDto expected = bookingResponseDtoFromRepo;

        assertEquals(expected, bookingService.getBooking(id1, id1));
    }

    @Test
    void getBooking_whenRequestWithIncorrectBookingId_thenThrowException() {
        when(bookingRepo.findById(999L)).thenThrow(BookingNotFoundException.class);
        when(bookingRepo.findById(-999L)).thenThrow(BookingNotFoundException.class);
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(999L, id1));
        assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(-999L, id1));
    }

    @Test
    void getBookings_whenRequestStateIsAll_thenReturnListWithDto() {
        List<BookingResponseDto> expectedListBookingResponseDtoAll = List.of(bookingResponseDtoFromRepo);
        when(bookingMapperService.prepareResponseDtoList(id2, StateForBooking.ALL, 0, 20))
                .thenReturn(expectedListBookingResponseDtoAll);

        List<BookingResponseDto> actualList = bookingService.getBookings(id2, StateForBooking.ALL, 0, 20);

        assertEquals(expectedListBookingResponseDtoAll, actualList);
    }

    @Test
    void getBookings_whenRequestStatusIsRejected_thenReturnListWithRejectedDto() {
        BookingResponseDto withRejectedState = bookingResponseDtoFromRepo;
        withRejectedState.setStatus(StatusOfBooking.REJECTED);
        List<BookingResponseDto> expectedListBookingResponseDtoRejected = List.of(withRejectedState);
        when(bookingMapperService.prepareResponseDtoList(id2, StateForBooking.REJECTED, 0, 20))
                .thenReturn(expectedListBookingResponseDtoRejected);

        List<BookingResponseDto> actualList = bookingService.getBookings(id2, StateForBooking.REJECTED, 0, 20);

        assertEquals(expectedListBookingResponseDtoRejected, actualList);
    }

    @Test
    void getBookingsForOwner_whenRequestStateIsAll_thenReturnListWithDto() {
        List<BookingResponseDto> expectedListBookingResponseDtoAll = List.of(bookingResponseDtoFromRepo);
        when(bookingMapperService.prepareResponseDtoListForOwner(id1, StateForBooking.ALL, 0, 20))
                .thenReturn(expectedListBookingResponseDtoAll);

        List<BookingResponseDto> actualList = bookingService.getBookingsForOwner(id1, StateForBooking.ALL, 0, 20);

        assertEquals(expectedListBookingResponseDtoAll, actualList);
    }


}