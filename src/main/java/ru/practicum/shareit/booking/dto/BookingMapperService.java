package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public final class BookingMapperService {
    ItemService itemService;
    UserService userService;
    BookingRepository bookingRepo;

    public Booking addStatusToBooking(Long ownerId, Long bookingId, Boolean approved) {

        if (!bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Объект не найден"))
                .getStatus().equals(StatusOfBooking.WAITING)) {
            log.info("Статус бронирования уже был установлен");
            throw new ValidationException("Secondary approval is prohibited!");
        }

        if (approved == null) {
            log.warn("статус подтверждения не может быть пустым");
            throw new ValidationException("Approve validation error. Status is null");
        }
        Booking bookingFromRepo = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Объект не найден"));
        if (!bookingFromRepo.getItem().getOwner().getId().equals(ownerId)) {
            log.info("Подтверждение статуса бронирования доступно только владельцу вещи");
            throw new BookingNotFoundException("Access error. Only Owner can approve booking");
        }
        bookingFromRepo.setStatus(approved.equals(true) ? StatusOfBooking.APPROVED
                : StatusOfBooking.REJECTED);
        return bookingFromRepo;
    }

    public Booking bookingRequestPrepareForAdd(Long bookerId, BookingRequestDto dto) {
        ItemDto itemDtoFromRepo = itemService.getItem(dto.getItemId(), bookerId);

        if (!itemDtoFromRepo.getAvailable()) {
            log.info("Вещь id {}, недоступна для бронирования", dto.getItemId());
            throw new ValidationException("Item  is not available for booking");
        }
        dateValidate(dto);
        dto.setStatus(StatusOfBooking.WAITING);

        UserDto userBooker = userService.getUser(bookerId);
        User owner = UserMapper.makeUserWithId(userService.getUser(itemDtoFromRepo.getOwnerId()))
                .orElseThrow(() -> new NullPointerException("объект не найден"));

        Item item = ItemMapper.makeItem(itemDtoFromRepo, owner)
                .orElseThrow(() -> new NullPointerException("объект не найден"));

        if (userBooker.getId().equals(item.getOwner().getId())) {
            log.info("Внимание! Попытка создать бронирование собственной вещи!");
            throw new BookingNotFoundException("Owner of item can't book it!");
        }

        User user = UserMapper.makeUserWithId(userBooker)
                .orElseThrow(() -> new NullPointerException("объект не найден"));

        return BookingMapper.requestDtoToEntity(dto, item, user)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    public void accessVerification(Booking bookingFromRepo, Long userId) {
        if (!(bookingFromRepo.getBooker().getId().equals(userId)
                || bookingFromRepo.getItem().getOwner().getId().equals(userId))) {
            log.info("Просмотр бронирования доступен только арендатору или владельцу вещи");
            throw new BookingNotFoundException("Access error. Only for Owner or Booker");
        }
    }

    public List<BookingResponseDto> prepareResponseDtoList(Long bookerId, String state) {
        userService.getUser(bookerId);
        List<Booking> responseBookingList;

        switch (state) {
            case "ALL":
                responseBookingList = bookingRepo.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case "FUTURE":
                responseBookingList = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now());
                break;
            case "CURRENT":
                responseBookingList = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                responseBookingList = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId,
                        LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                responseBookingList = bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId);
                responseBookingList = responseBookingList.stream()
                        .filter(booking -> booking.getStatus().equals(StatusOfBooking.valueOf(state)))
                        .collect(Collectors.toList());
                break;

            default:
                log.warn("Статус запроса {} не поддерживается", state);
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        return responseBookingList.stream()
                .map(booking -> BookingMapper.entityToResponseDto(booking)
                        .orElseThrow(() -> new NullPointerException("dto объект не найден")))
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> prepareResponseDtoListForOwner(Long ownerId, String state) {
        userService.getUser(ownerId);
        if (itemService.getItems(ownerId).size() == 0) {
            log.info("Пользователь {} не владеет вещами", ownerId);
            throw new ItemNotFoundException("Items of user is not found!");
        }

        List<Booking> bookingsOfOwnersItems = new ArrayList<>();

        switch (state) {
            case "ALL":
                bookingsOfOwnersItems = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "FUTURE":
                bookingsOfOwnersItems =
                        bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "CURRENT":
                bookingsOfOwnersItems =
                        bookingRepo.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                                LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookingsOfOwnersItems =
                        bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "WAITING":
            case "REJECTED":
                bookingsOfOwnersItems =
                        bookingRepo.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                bookingsOfOwnersItems = bookingsOfOwnersItems.stream()
                        .filter(booking -> booking.getStatus().equals(StatusOfBooking.valueOf(state)))
                        .collect(Collectors.toList());

                break;
            default:
                log.warn("Статус запроса {} не поддерживается", state);
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingsOfOwnersItems.stream()
                .map(booking -> BookingMapper.entityToResponseDto(booking)
                        .orElseThrow(() -> new NullPointerException("dto объект не найден")))
                .collect(Collectors.toList());
    }

    private void dateValidate(BookingRequestDto dto) {
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            log.warn("Время начала бронирования не может быть в прошлом");
            throw new ValidationException("StartTime can't be from the past");
        }
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().equals(dto.getStart())) {
            log.warn("Окончание бронирования должно быть позже начала бронирования");
            throw new ValidationException("EndTime can be later then StartDate");
        }
    }
}
