package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperServiceTest {
    private ItemRequestDto dtoToPrepareItemRequest;
    private UserDto userDtoRequester;
    private User requester;
    private ItemRequest expectedItemRequest;
    private LocalDateTime created = LocalDateTime.now();

    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepo;
    @InjectMocks
    private ItemRequestMapperService itemRequestMapperService;

    @BeforeEach
    void setUp() {
        dtoToPrepareItemRequest = ItemRequestDto.builder()
                .description("description")
                .build();

        userDtoRequester = UserDto.builder()
                .id(1L)
                .name("Requester")
                .email("r@r.r")
                .build();

        requester = UserMapper.makeUserWithId(userDtoRequester).orElseThrow();

        expectedItemRequest = ItemRequest.builder()
                .requester(requester)
                .description(dtoToPrepareItemRequest.getDescription())
                .build();
    }

    @Test
    void prepareForSaveItemRequest_whenIncomeCorrect_thenReturnEntity() {
        when(userService.getUser(1L)).thenReturn(userDtoRequester);
        assertEquals(expectedItemRequest,
                itemRequestMapperService.prepareForSaveItemRequest(1L, dtoToPrepareItemRequest));
        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void prepareForSaveItemRequest_whenUserNotFound_thenThrowsException() {
        when(userService.getUser(999L)).thenThrow(UserNotFoundException.class);
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemRequestMapperService.prepareForSaveItemRequest(999L, dtoToPrepareItemRequest));
        userNotFoundException.getMessage();

        verify(userService, times(1)).getUser(999L);
    }

    @Test
    void prepareForSaveItemRequest_whenUserIncorrect_thenThrowsException() {
        when(userService.getUser(-999L)).thenThrow(UserNotFoundException.class);
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemRequestMapperService.prepareForSaveItemRequest(-999L, dtoToPrepareItemRequest));
        userNotFoundException.getMessage();

        verify(userService, times(1)).getUser(-999L);
    }

    @Test
    void requesterValidate_whenUserIdCorrect_thenReturnTrue() {
        when(userService.getUser(1L)).thenReturn(userDtoRequester);
        assertTrue(itemRequestMapperService.requesterValidate(1L));
    }

    @Test
    void requesterValidate_whenUserIdNotValid_thenReturnFalse() {
        IncorrectIdException incorrectIdException = assertThrows(IncorrectIdException.class,
                () -> itemRequestMapperService.requesterValidate(-1L));
        incorrectIdException.getMessage();

        verify(userService, never()).getUser(-1L);
    }

    @Test
    void prepareForReturnDto_whenIncomeRequestIsCorrect_thenReturnDto() {
        User owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("o@o.o")
                .build();

        Item item = Item.builder()
                .owner(owner)
                .name("ItemName")
                .isAvailable(true)
                .description("some item")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("some item")
                .requester(requester)
                .created(created)
                .build();

        List<Item> itemsForRequest = List.of(item);
        List<ItemDto> itemDtoList = List.of(ItemMapper.makeDtoFromItem(item).get());

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .requesterId(1L)
                .description(itemRequest.getDescription())
                .items(itemDtoList)
                .created(created)
                .id(1L)
                .build();

        when(itemRepo.findAllByRequestId(1L)).thenReturn(itemsForRequest);
        assertEquals(itemRequestDto, itemRequestMapperService.prepareForReturnDto(itemRequest));
    }

    @Test
    void prepareForReturnDto_whenItemNotFound_thenReturnDtoWithEmptyList() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("some item")
                .requester(requester)
                .created(created)
                .build();

        List<Item> itemsForRequest = new ArrayList<>();
        List<ItemDto> itemDtoList = new ArrayList<>();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .requesterId(1L)
                .description(itemRequest.getDescription())
                .items(itemDtoList)
                .created(created)
                .id(1L)
                .build();

        when(itemRepo.findAllByRequestId(1L)).thenReturn(itemsForRequest);
        assertEquals(itemRequestDto, itemRequestMapperService.prepareForReturnDto(itemRequest));
    }


    @Test
    void prepareForReturnListDto() {
        List<ItemDto> itemsDto = new ArrayList<>();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("desc")
                .requesterId(1L)
                .created(created)
                .items(itemsDto)
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .requester(requester)
                .created(created)
                .build();

        List<ItemRequest> itemRequests = List.of(itemRequest);

        List<ItemRequestDto> actual = itemRequestMapperService.prepareForReturnListDto(itemRequests);
        assertEquals(List.of(itemRequestDto), actual);
    }
}