package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateForBooking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class BookingMapperService {
    private ItemService itemService;
    private UserService userService;
    private BookingRepository bookingRepo;

    public Booking addStatusToBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking bookingFromRepo = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Объект не найден"));

        if (!bookingFromRepo.getStatus().equals(StatusOfBooking.WAITING)) {
            log.info("Статус бронирования уже был установлен");
            throw new ValidationException("Secondary approval is prohibited!");
        }

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
        User owner = UserMapper.makeUserWithId(userService.getUser(itemDtoFromRepo.getOwnerId())).get();

        Item item = ItemMapper.makeItem(itemDtoFromRepo, owner).get();

        if (userBooker.getId().equals(item.getOwner().getId())) {
            log.info("Внимание! Попытка создать бронирование собственной вещи!");
            throw new BookingNotFoundException("Owner of item can't book it!");
        }

        User user = UserMapper.makeUserWithId(userBooker).get();

        return BookingMapper.requestDtoToEntity(dto, item, user).get();
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

    public void accessVerification(Booking bookingFromRepo, Long userId) {
        if (!(bookingFromRepo.getBooker().getId().equals(userId)
                || bookingFromRepo.getItem().getOwner().getId().equals(userId))) {
            log.info("Просмотр бронирования доступен только арендатору или владельцу вещи");
            throw new BookingNotFoundException("Access error. Only for Owner or Booker");
        }
    }

    public List<BookingResponseDto> prepareResponseDtoList(Long bookerId, StateForBooking state,
                                                           Integer from, Integer size) {
        userService.getUser(bookerId);
        List<Booking> answerPage;
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case FUTURE:
                answerPage = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(),
                        pageRequest);
                break;
            case CURRENT:
                answerPage = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                answerPage = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(),
                        pageRequest);
                break;
            case WAITING:
            case REJECTED:
                StatusOfBooking status
                        = state.equals(StateForBooking.WAITING) ? StatusOfBooking.WAITING : StatusOfBooking.REJECTED;
                answerPage = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, pageRequest, status);
                break;
            case ALL:
            default:
                answerPage = bookingRepo.findByBookerIdOrderByStartDesc(bookerId, pageRequest);
                break;
        }

        assert answerPage != null;
        List<Booking> responseBookingList = answerPage.stream()
                .collect(Collectors.toList());

        return responseBookingList.stream()
                .map(booking -> BookingMapper.entityToResponseDto(booking)
                        .orElseThrow(() -> new BookingNotFoundException("dto объект не найден")))
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> prepareResponseDtoListForOwner(Long ownerId, StateForBooking state, Integer from, Integer size) {
        userService.getUser(ownerId);
        List<Booking> answerPage;
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        if (itemService.getItems(ownerId).size() == 0) {
            log.info("Пользователь {} не владеет вещами", ownerId);
            throw new ItemNotFoundException("Items of user is not found!");
        }

        switch (state) {

            case WAITING:
            case REJECTED:
                StatusOfBooking status
                        = state.equals(StateForBooking.WAITING) ? StatusOfBooking.WAITING : StatusOfBooking.REJECTED;
                answerPage = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, pageRequest, status);
                break;
            case FUTURE:
                answerPage = bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                answerPage = bookingRepo.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                answerPage = bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId,
                        LocalDateTime.now(), pageRequest);
                break;
            case ALL:
            default:
                answerPage = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageRequest);
                break;
        }

        assert answerPage != null;
        List<Booking> responseBookingList = answerPage.stream()
                .collect(Collectors.toList());

        return responseBookingList != null ?
                responseBookingList.stream()
                        .map(booking -> BookingMapper.entityToResponseDto(booking)
                                .orElseThrow(() -> new BookingNotFoundException("dto объект не найден")))
                        .collect(Collectors.toList()) : Collections.emptyList();
    }
}
