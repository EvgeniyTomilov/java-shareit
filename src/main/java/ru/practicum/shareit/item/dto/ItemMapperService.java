package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.IncorrectItemDtoException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ItemMapperService {
    private UserService userService;
    private CommentRepository commentRepo;
    private BookingRepository bookingRepo;
    private ItemRepository itemRepo;

    public Item addNewItem(Long ownerId, ItemDto itemDto) {
        itemDtoValidate(ownerId, itemDto);
        User user = UserMapper.makeUserWithId(userService.getUser(ownerId))
                .orElseThrow(() -> new NullPointerException("объект не найден"));

        return ItemMapper.makeItem(itemDto, user)
                .orElseThrow(() -> new NullPointerException("объект не найден"));
    }

    public ItemDto getItemDto(Item item, Long userId) {
        List<CommentDto> commentsForItemDto = findCommentsToItem(item);
        if (item.getOwner().getId().equals(userId)) {
            return getItemDtoForOwner(item, userId, commentsForItemDto);
        } else {
            return getItemDtoForUser(item, commentsForItemDto);
        }
    }

    private void itemDtoValidate(long userId, ItemDto itemDto) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();

        if (StringUtils.isBlank(name)) {
            log.warn("Item's name {} can't be null!", itemDto);
            throw new IncorrectItemDtoException("Item's name is not found");
        }
        if (StringUtils.isBlank(description)) {
            log.warn("Item's description {} can't be null!", itemDto);
            throw new IncorrectItemDtoException("Item's description is not found");
        }
        if (itemDto.getAvailable() == null) {
            log.warn("Available-status of item {} can't be null!", itemDto);
            throw new IncorrectItemDtoException("Available-status of item not found");
        }
        userService.getUser(userId);
    }

    public List<ItemDto> getItems(List<Item> allItems) {
        return allItems.stream()
                .map(item -> ItemMapper.makeDtoFromItemWithBooking(item, findCommentsToItem(item),
                                findLastBooking(item), findNextBooking(item))
                        .orElseThrow(() -> new NullPointerException("dto объект не найден")))
                .collect(Collectors.toList());
    }

    public ItemDto getItemDtoForUser(Item item, List<CommentDto> commentsForItemDto) {
        return ItemMapper.makeDtoFromItemWithComment(item, commentsForItemDto)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    public ItemDto getItemDtoForOwner(Item item, Long userId, List<CommentDto> commentsForItemDto) {
        return ItemMapper.makeDtoFromItemWithBooking(item, commentsForItemDto,
                        findLastBooking(item), findNextBooking(item))
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    public Item prepareItemToUpdate(Long ownerId, Long itemId, ItemDto itemDtoWithUpdate) {
        validateId(itemId);
        validateId(ownerId);
        User owner = UserMapper.makeUserWithId(userService.getUser(ownerId))
                .orElseThrow(() -> new NullPointerException("объект не найден"));
        ItemDto itemDtoFromRepo = getItemForUpdate(itemId);

        return ItemMapper.makeItemForUpdate(itemDtoFromRepo, itemDtoWithUpdate, owner)
                .orElseThrow(() -> new NullPointerException("объект не найден"));
    }

    private BookingForItemDto findNextBooking(Item item) {
        List<Booking> allNextBooking = bookingRepo.findAllByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                LocalDateTime.now());
        allNextBooking = allNextBooking.stream()
                .filter(booking -> booking.getStatus().equals(StatusOfBooking.APPROVED))
                .collect(Collectors.toList());
        BookingForItemDto nextBooking = new BookingForItemDto();
        if (allNextBooking.size() > 0) {
            nextBooking = BookingMapper.entityToBookingForItemDto(allNextBooking.get(0))
                    .orElseThrow(() -> new NullPointerException("dto объект не найден"));
        } else nextBooking = null;
        return nextBooking;
    }

    private BookingForItemDto findLastBooking(Item item) {
        List<Booking> allLastBooking = bookingRepo.findAllByItemIdAndStartBeforeOrderByStart(item.getId(),
                LocalDateTime.now());
        allLastBooking = allLastBooking.stream()
                .filter(booking -> booking.getStatus().equals(StatusOfBooking.APPROVED))
                .collect(Collectors.toList());
        BookingForItemDto lastBooking = new BookingForItemDto();
        if (allLastBooking.size() > 0) {
            lastBooking = BookingMapper.entityToBookingForItemDto(allLastBooking.get(allLastBooking.size() - 1))
                    .orElseThrow(() -> new NullPointerException("dto объект не найден"));
        } else lastBooking = null;
        return lastBooking;
    }

    private List<CommentDto> findCommentsToItem(Item item) {
        List<Comment> commentsForItem = commentRepo.findAllByItemIdOrderById(item.getId());
        return commentsForItem.stream()
                .map(CommentMapper::entityToDto)
                .collect(Collectors.toList());
    }

    private ItemDto getItemForUpdate(Long itemId) {
        Item item = itemRepo.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Item is not found"));
        return getItemDtoForUser(item, findCommentsToItem(item));
    }

    private void validateId(Long id) {
        if (id < 1) {
            log.warn("id {} incorrect", id);
            throw new IncorrectIdException("id can't be less then 1");
        }
    }

    public Comment prepareCommentToSave(CommentRequestDto requestDto) {
        User author = UserMapper.makeUserWithId(userService.getUser(requestDto.getAuthorId()))
                .orElseThrow(() -> new NullPointerException("объект не найден"));
        List<Booking> endedBookingOfAuthor =
                bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(author.getId(), LocalDateTime.now()).stream()
                        .filter(booking -> booking.getItem().getId().equals(requestDto.getItemId()))
                        .collect(Collectors.toList());
        if (endedBookingOfAuthor.size() == 0) {
            log.info("Добавить отзыв можно только после завершения бронирования вещи!");
            throw new ValidationException("User can't add comment without booking completed!");
        }

        Item item = itemRepo.findById(requestDto.getItemId())
                .orElseThrow(() -> new NullPointerException("Объект не найден"));

        return CommentMapper.requestToEntity(item, author, requestDto.getText());
    }
}
