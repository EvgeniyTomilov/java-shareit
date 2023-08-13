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
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ItemMapperServiceImpl implements ItemMapperService {
    private final UserService userService;
    private final CommentRepository commentRepo;
    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final ItemRequestRepository itemRequestRepo;

    @Override
    public Item addNewItem(Long ownerId, ItemDto itemDto) {
        itemDtoValidate(ownerId, itemDto);
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

    @Override
    public ItemDto getItemDto(Item item, Long userId) {
        List<CommentDto> commentsForItemDto = findCommentsToItem(item);
        if (item.getOwner().getId().equals(userId)) {
            return getItemDtoForOwner(item, commentsForItemDto);
        } else {
            return getItemDtoForUser(item, commentsForItemDto);
        }
    }

    //    @Override
//    public List<ItemDto> getItems(List<Item> allItems) {
//        List<BookingForItemDto> allBookings = findAllBookings();
//        List<CommentDto> allComments = findAllComments();
//        List<ItemDto> itemDtos = new ArrayList<>();
//
//        for (Item item : allItems) {
//            BookingForItemDto nextBooking = allBookings.stream()
//                    .filter(b -> Objects.equals(b.getItem().getId(), item.getId()))
//                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
//                    .min(Comparator.comparing(BookingForItemDto::getStart))
//                    .orElse(null);
//
//            BookingForItemDto lastBooking = allBookings.stream()
//                    .filter(b -> Objects.equals(b.getItem().getId(), item.getId()))
//                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
//                    .max(Comparator.comparing(BookingForItemDto::getStart))
//                    .orElse(null);
//
//            List<CommentDto> allCommentsById = allComments
//                    .stream()
//                    .filter(c -> Objects.equals(c.getItem().getId(), item.getId()))
//                    .collect(Collectors.toList());
//
//            itemDtos.add(ItemMapper.makeDtoFromItemWithBooking(
//                    item,
//                    allCommentsById,
//                    lastBooking,
//                    nextBooking).orElse(null));
//        }
//
//
//        return itemDtos;
//    }
    public List<ItemDto> getItems(List<Item> allItems) {
        return allItems.stream()
                .map(item -> ItemMapper.makeDtoFromItemWithBooking(item, findCommentsToItem(item),
                        findLastBooking(item), findNextBooking(item)).get())
                .collect(Collectors.toList());
    }

//    private List<CommentDto> findAllComments() {
//        return commentRepo.findAll()
//                .stream()
//                .map(CommentMapper::entityToDto)
//                .collect(Collectors.toList());
//    }
//
//    private List<BookingForItemDto> findAllBookings() {
//        List<Booking> allBookings = bookingRepo.findAll();
//        return allBookings.stream()
//                .map(booking -> BookingMapper.entityToBookingForItemDto(booking).orElse(null))
//                .collect(Collectors.toList());
//    }

    @Override
    public ItemDto getItemDtoForUser(Item item, List<CommentDto> commentsForItemDto) {
        return ItemMapper.makeDtoFromItemWithComment(item, commentsForItemDto).get();
    }

    @Override
    public ItemDto getItemDtoForOwner(Item item, List<CommentDto> commentsForItemDto) {
        return ItemMapper.makeDtoFromItemWithBooking(item, commentsForItemDto,
                findLastBooking(item), findNextBooking(item)).get();
    }

    @Override
    public Item prepareItemToUpdate(Long ownerId, Long itemId, ItemDto itemDtoWithUpdate) {
        validateId(itemId);
        validateId(ownerId);
        User owner = UserMapper.makeUserWithId(userService.getUser(ownerId)).get();
        ItemDto itemDtoFromRepo = getItemForUpdate(itemId);

        return ItemMapper.makeItemForUpdate(itemDtoFromRepo, itemDtoWithUpdate, owner).get();
    }

    @Override
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

    private void validateId(Long id) {
        if (id < 1) {
            log.warn("id {} incorrect", id);
            throw new IncorrectIdException("id can't be less then 1");
        }
    }
}