package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    final long itemRequestId1 = 1L;
    final long requesterId1 = 1L;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();
    private final ItemRequestDto itemRequestDtoResponse = new ItemRequestDto();
    private final ItemRequest itemRequest = new ItemRequest();
    private final ItemRequest expectedItemRequest = new ItemRequest();
    private final User user = new User();
    private List<ItemRequest> expectedList = new ArrayList<>();
    private List<ItemRequestDto> expectedListDto = new ArrayList<>();
    private ItemRequestDto expectedItemRequestDto = new ItemRequestDto();
    @Mock
    private ItemRequestMapperService itemRequestMapperService;
    @Mock
    private ItemRequestRepository itemRequestRepo;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setUp() {
        user.setId(requesterId1);
        user.setName("testUser");
        user.setEmail("test@test.ru");

        itemRequest.setRequester(user);
        itemRequestDto.setDescription("first test");

        itemRequestDtoResponse.setId(1L);
        itemRequestDtoResponse.setDescription("first test");
        itemRequestDtoResponse.setRequesterId(1L);

        expectedItemRequest.setId(itemRequestId1);
        expectedItemRequest.setRequester(user);
        expectedItemRequest.setDescription("first test");

        expectedItemRequestDto = ItemRequestMapper.makeItemRequestDto(expectedItemRequest).orElseThrow();

        expectedList.add(expectedItemRequest);
        expectedListDto.add(expectedItemRequestDto);
    }

    @AfterEach
    void afterEach() {
        if (!expectedList.isEmpty()) {
            expectedList.clear();
        }
        if (!expectedListDto.isEmpty()) {
            expectedListDto.clear();
        }
    }

    @Test
    void getItemRequest_whenEntityFound_thenReturnDto() {
        when(itemRequestMapperService.requesterValidate(1L)).thenReturn(true);
        when(itemRequestRepo.findById(1L)).thenReturn(Optional.of(expectedItemRequest));
        when(itemRequestMapperService.prepareForReturnDto(expectedItemRequest)).thenReturn(expectedItemRequestDto);

        ItemRequestDto actualRequestDto = itemRequestService.getItemRequest(requesterId1, itemRequestId1);
        assertEquals(expectedItemRequestDto, actualRequestDto);
    }

    @Test
    void addNewItemRequest_whenRequestIsCorrect_thenReturnDto() {
        ItemRequest expectedItemRequestForSave = ItemRequest.builder()
                .description("first test")
                .requester(user)
                .build();

        when(itemRequestMapperService.prepareForSaveItemRequest(requesterId1, itemRequestDto))
                .thenReturn(expectedItemRequestForSave);
        when(itemRequestRepo.save(expectedItemRequestForSave)).thenReturn(expectedItemRequest);
        when(itemRequestMapperService.prepareForReturnDto(expectedItemRequest)).thenReturn(expectedItemRequestDto);

        ItemRequestDto actualRequestDto = itemRequestService.addNewItemRequest(requesterId1, itemRequestDto);

        assertEquals(expectedItemRequestDto, actualRequestDto);
    }

    @Test
    void addNewItemRequest_withWrongUser_thenUserNotFoundExceptionThrown() {
        when(itemRequestMapperService.prepareForSaveItemRequest(999L, itemRequestDto))
                .thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.addNewItemRequest(999L, itemRequestDto));
    }


    @Test
    void addNewItemRequest_withoutDescription_thenValidationExceptionExceptionThrown() {
        itemRequestDto.setDescription(null);
        when(itemRequestMapperService.prepareForSaveItemRequest(requesterId1, itemRequestDto))
                .thenThrow(ValidationException.class);

        assertThrows(ValidationException.class,
                () -> itemRequestService.addNewItemRequest(requesterId1, itemRequestDto));
        verify(itemRequestMapperService).prepareForSaveItemRequest(requesterId1, itemRequestDto);
        verify(itemRequestRepo, never()).save(itemRequest);
    }

    @Test
    void getItemRequests_whenRequestIsFound_thenReturnListWithDto() {
        when(itemRequestMapperService.requesterValidate(1L)).thenReturn(true);
        when(itemRequestRepo.findAllByRequesterId(requesterId1)).thenReturn(expectedList);
        when(itemRequestMapperService.prepareForReturnListDto(expectedList))
                .thenReturn(List.of(itemRequestDtoResponse));
        List<ItemRequestDto> actual = itemRequestService.getItemRequests(requesterId1);
        assertEquals(expectedListDto, actual);
    }

    @Test
    void getItemRequests_whenRequestIsNotFound_thenThrowItemRequestNotFoundException() {
        when(itemRequestMapperService.requesterValidate(1L)).thenReturn(true);
        ItemRequestNotFoundException ex = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, 999L));
    }

    @Test
    void getItemRequests_whenUserHasNoRequests_thenReturnEmptyList() {
        when(itemRequestMapperService.requesterValidate(1L)).thenReturn(true);
        when(itemRequestRepo.findAllByRequesterId(requesterId1)).thenReturn(List.of(new ItemRequest()));
        when(itemRequestMapperService.prepareForReturnListDto(List.of(new ItemRequest())))
                .thenReturn(List.of(new ItemRequestDto()));
        List<ItemRequestDto> actual = itemRequestService.getItemRequests(requesterId1);
        assertEquals(List.of(new ItemRequestDto()), actual);
    }
}