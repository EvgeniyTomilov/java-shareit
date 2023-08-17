package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemMapperServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private ItemRepository itemRepo;
    @Mock
    private ItemRequestRepository itemRequestRepo;
    @InjectMocks
    private ItemMapperService itemMapperService;

    private User userOwner;
    private User userBooker;
    private Item item;
    private Long ownerId = 1L;
    private Long bookerId = 2L;
    private ItemDto itemDtoValid;
    private User userRequester;
    private UserDto userDtoRequester;
    private ItemRequest itemRequest;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime time = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        userOwner = User.builder()
                .id(ownerId)
                .email("o@o.ru")
                .name("OwnerUser")
                .build();

        userBooker = User.builder()
                .id(bookerId)
                .email("b@b.ru")
                .name("BookerUser")
                .build();

        userDtoRequester = UserDto.builder()
                .id(1L)
                .name("Requester")
                .email("r@r.r")
                .build();

        userRequester = UserMapper.makeUserWithId(userDtoRequester).orElseThrow();

        itemRequest = ItemRequest.builder()
                .requester(userRequester)
                .description("description")
                .created(created)
                .build();

        item = Item.builder()
                .description("item description")
                .isAvailable(true)
                .id(1L)
                .name("test item")
                .owner(userOwner)
                .build();

        itemDtoValid = ItemDto.builder()
                .available(true)
                .name(item.getName())
                .description(item.getDescription())
                .requestId(1L)
                .build();

    }

    @Test
    void prepareItemToUpdate_whenRequestIsCorrect_thenReturnItem() {
        ItemDto itemDtoWithUpdate = ItemDto.builder()
                .description("item description")
                .available(true)
                .id(1L)
                .name("test item update")
                .ownerId(ownerId)
                .build();

        item.setName("test item update");

        when(userService.getUser(ownerId)).thenReturn(UserMapper.makeDto(userOwner).orElseThrow());
        when(itemRepo.findById(1L)).thenReturn(Optional.ofNullable(item));

        assertEquals(item, itemMapperService.prepareItemToUpdate(ownerId, 1L, itemDtoWithUpdate));
    }

    @Test
    void addNewItem_whenRequestIdNull_thenReturnEntityWithoutRequest() {
        UserDto userDto = UserMapper.makeDto(userOwner).orElseThrow();
        Item expectedItem = new Item(1L, userOwner, item.getName(),
                item.getDescription(), true, null);

        itemDtoValid.setRequestId(null);
        when(userService.getUser(ownerId)).thenReturn(userDto);

        assertEquals(expectedItem, itemMapperService.addNewItem(ownerId, itemDtoValid));

    }

    @Test
    void addNewItem_whenRequestCorrect_thenReturnEntity() {
        UserDto userDto = UserMapper.makeDto(userOwner).orElseThrow();
        when(userService.getUser(ownerId)).thenReturn(userDto);
        when(itemRequestRepo.findById(1L)).thenReturn(Optional.ofNullable(itemRequest));

        assertEquals(item, itemMapperService.addNewItem(ownerId, itemDtoValid));
    }

    @Test
    void getItemDto_whenCorrect_thenReturnDto() {
        ItemDto expectedItemDto = ItemMapper.makeDtoFromItem(item).orElseThrow();
        assertEquals(expectedItemDto, itemMapperService.getItemDto(item, 1L));
        assertEquals(expectedItemDto, itemMapperService.getItemDto(item, 2L));
    }

    @Test
    void getItems_whenListItemCorrect_thenReturnListDto() {
        List<Item> listItem = List.of(item);
        BookingForItemDto lastBooking = new BookingForItemDto();
        BookingForItemDto nextBooking = new BookingForItemDto();
        ItemDto itemDto = new ItemDto(1L, "test item", "item description", true,
                1L, null, new ArrayList<>(), lastBooking, nextBooking);
        List<ItemDto> expected = List.of(itemDto);
        assertEquals(expected, itemMapperService.getItems(listItem));
    }

    @Test
    void getItemDtoForOwner_whenIncomeRequestCorrect_thenReturnItem() {
        BookingForItemDto lastBooking = new BookingForItemDto();
        BookingForItemDto nextBooking = new BookingForItemDto();
        ItemDto expectedItemDto = new ItemDto(1L, "test item", "item description", true,
                1L, null, new ArrayList<>(), lastBooking, nextBooking);
        assertEquals(expectedItemDto, itemMapperService.getItemDtoForOwner(item, new ArrayList<>()));
    }

    @Test
    void getItemDtoForUser_whenIncomeRequestCorrect_thenReturnItem() {
        BookingForItemDto lastBooking = new BookingForItemDto();
        BookingForItemDto nextBooking = new BookingForItemDto();
        ItemDto expectedItemDto = new ItemDto(1L, "test item", "item description", true,
                1L, null, new ArrayList<>(), lastBooking, nextBooking);
        assertEquals(expectedItemDto, itemMapperService.getItemDtoForUser(item, new ArrayList<>()));
    }

    @Test
    void getItems_whenNextBookingNotFound_thenReturnListWithItemWithNullLastBooking() {
        BookingForItemDto lastBooking = null;
        BookingForItemDto nextBooking = null;
        ItemDto expectedItemDto = new ItemDto(1L, "test item", "item description", true,
                1L, null, new ArrayList<>(), lastBooking, nextBooking);
        Item item = ItemMapper.makeItem(expectedItemDto, userOwner).get();

        List<Booking> nextList = new ArrayList<>();

        when(bookingRepo.findAllByItemIdAndStartAfterOrderByStartAsc(eq(item.getId()),
                Mockito.any(LocalDateTime.class))).thenReturn(nextList);

        assertEquals(List.of(expectedItemDto), itemMapperService.getItems(List.of(item)));
    }

    @Test
    void prepareCommentToSave_whenCorrectIncome_thenReturnComment() {
        Long authorId = 3L;
        User author = User.builder()
                .id(authorId)
                .name("Author Name")
                .email("a@a.a")
                .build();

        UserDto authorDto = UserMapper.makeDto(author).orElseThrow();

        Booking booking = Booking.builder()
                .booker(author)
                .id(1L)
                .status(StatusOfBooking.APPROVED)
                .start(time.minusDays(3))
                .end(time.minusDays(1))
                .item(item)
                .build();

        Comment comment = Comment.builder()
                .text("first comment")
                .author(author)
                .item(item)
                .build();

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()

                .text(comment.getText())
                .itemId(1L)
                .authorId(authorId)
                .build();

        List<Booking> endedBookingOfAuthor = List.of(booking);

        when(userService.getUser(authorId)).thenReturn(authorDto);
        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(authorId), Mockito.any(LocalDateTime.class)))
                .thenReturn(endedBookingOfAuthor);
        when(itemRepo.findById(1L)).thenReturn(Optional.ofNullable(item));

        Comment commentResult = itemMapperService.prepareCommentToSave(commentRequestDto);

        comment.setCreated(commentResult.getCreated());

        assertEquals(comment, commentResult);
    }

    @Test
    void prepareCommentToSave_whenBookingListEmpty_thenThrowValidationException() {
        Long authorId = 3L;
        User author = User.builder()
                .id(authorId)
                .name("Author Name")
                .email("a@a.a")
                .build();

        UserDto authorDto = UserMapper.makeDto(author).orElseThrow();

        Comment comment = Comment.builder()
                .text("first comment")
                .author(author)
                .item(item)
                .build();

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()

                .text(comment.getText())
                .itemId(1L)
                .authorId(authorId)
                .build();

        List<Booking> endedBookingOfAuthor = new ArrayList<>();

        when(userService.getUser(authorId)).thenReturn(authorDto);
        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(authorId), Mockito.any(LocalDateTime.class)))
                .thenReturn(endedBookingOfAuthor);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemMapperService.prepareCommentToSave(commentRequestDto));
        ex.getMessage();
    }
}