package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserService;

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
    private ItemRequestRepository itemRequestRepo;

    public Item addNewItem(Long ownerId, ItemDto itemDto) {
        User owner = UserMapper.makeUserWithId(userService.getUser(ownerId)).get();
        Item item = new Item();
        if (itemDto.getRequestId() == null) {
            item = ItemMapper.makeItem(itemDto, owner).get();
        } else {
            ItemRequest request = itemRequestRepo.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException("ItemRequest Not Found!"));
            item = ItemMapper.makeItemWithRequest(itemDto, owner, request).get();
        }

        return item;
    }

    public ItemDto getItemDto(Item item, Long userId) {
        List<CommentDto> commentsForItemDto = findCommentsToItem(item);
        if (item.getOwner().getId().equals(userId)) {
            return getItemDtoForOwner(item, commentsForItemDto);
        } else {
            return getItemDtoForUser(item, commentsForItemDto);
        }
    }

    public List<ItemDto> getItems(List<Item> allItems) {
        return allItems.stream()
                .map(item -> ItemMapper.makeDtoFromItemWithBooking(item, findCommentsToItem(item),
                        findLastBooking(item), findNextBooking(item)).get())
                .collect(Collectors.toList());
    }

    public ItemDto getItemDtoForUser(Item item, List<CommentDto> commentsForItemDto) {
        return ItemMapper.makeDtoFromItemWithComment(item, commentsForItemDto).get();
    }

    public ItemDto getItemDtoForOwner(Item item, List<CommentDto> commentsForItemDto) {
        return ItemMapper.makeDtoFromItemWithBooking(item, commentsForItemDto,
                findLastBooking(item), findNextBooking(item)).get();
    }

    public Item prepareItemToUpdate(Long ownerId, Long itemId, ItemDto itemDtoWithUpdate) {
        User owner = UserMapper.makeUserWithId(userService.getUser(ownerId)).get();
        ItemDto itemDtoFromRepo = getItemForUpdate(itemId);

        return ItemMapper.makeItemForUpdate(itemDtoFromRepo, itemDtoWithUpdate, owner).get();
    }

    private BookingForItemDto findNextBooking(Item item) {
        List<Booking> allNextBooking = bookingRepo.findAllByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                LocalDateTime.now());
        allNextBooking = allNextBooking.stream()
                .filter(booking -> booking.getStatus().equals(StatusOfBooking.APPROVED))
                .collect(Collectors.toList());
        BookingForItemDto nextBooking;
        if (allNextBooking.size() > 0) {
            nextBooking = BookingMapper.entityToBookingForItemDto(allNextBooking.get(0)).get();
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
            lastBooking = BookingMapper.entityToBookingForItemDto(allLastBooking.get(allLastBooking.size() - 1)).get();
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
        ItemDto itemDto = getItemDtoForUser(item, findCommentsToItem(item));
        return itemDto;
    }

    public Comment prepareCommentToSave(CommentRequestDto requestDto) {
        User author = UserMapper.makeUserWithId(userService.getUser(requestDto.getAuthorId())).get();
        List<Booking> endedBookingOfAuthor =
                bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(author.getId(), LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getItem().getId().equals(requestDto.getItemId()))
                        .collect(Collectors.toList());
        if (endedBookingOfAuthor.size() == 0) {
            log.info("Добавить отзыв можно только после завершения бронирования вещи!");
            throw new ValidationException("User can't add comment without booking completed!");
        }

        Item item = itemRepo.findById(requestDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Объект не найден"));

        Comment comment = CommentMapper.requestToEntity(item, author, requestDto.getText());
        return comment;
    }
}
