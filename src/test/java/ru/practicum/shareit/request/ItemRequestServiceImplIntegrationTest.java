package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestServiceImplIntegrationTest {
    final long itemRequestId1 = 1L;
    final long requesterId1 = 1L;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();
    private final ItemRequestDto itemRequestDtoResponse = new ItemRequestDto();
    private final ItemRequest itemRequest = new ItemRequest();
    private final ItemRequest expectedItemRequest = new ItemRequest();
    private final User user = new User();

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;


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

    }

    @Test
    void whenAddNewItemRequest() {
        userRepository.save(user);

        ItemRequestDto expected = itemRequestService.addNewItemRequest(user.getId(), itemRequestDto);
        ItemRequestDto actual = itemRequestService.getItemRequest(user.getId(), expected.getRequesterId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("created").isEqualTo(expected);
    }

    @Test
    void whenGetItemRequests() {
        userRepository.save(user);
        ItemRequestDto dtoForSave = ItemRequestDto.builder()
                .requesterId(user.getId())
                .description("description")
                .build();
        ItemRequestDto itemRequestDto1 = itemRequestService.addNewItemRequest(user.getId(), itemRequestDto);
        ItemRequestDto itemRequestDto2 = itemRequestService.addNewItemRequest(user.getId(), dtoForSave);
        List<ItemRequestDto> expected = List.of(itemRequestDto1, itemRequestDto2);

        List<ItemRequestDto> actual = itemRequestService.getItemRequests(user.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("created").isEqualTo(expected);
    }

    @Test
    void whenGetAllItemRequests() {
        userRepository.save(user);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequestDto dtoForSave = ItemRequestDto.builder()
                .requesterId(user.getId())
                .description("description")
                .created(LocalDateTime.now().plusDays(10))
                .build();
        ItemRequestDto itemRequestDto1 = itemRequestService.addNewItemRequest(user.getId(), itemRequestDto);
        ItemRequestDto itemRequestDto2 = itemRequestService.addNewItemRequest(user.getId(), dtoForSave);
        List<ItemRequestDto> expected = List.of(itemRequestDto1, itemRequestDto2);

        List<ItemRequestDto> actual = itemRequestService.getAllItemRequests(3L, 0, 2);

        assertThat(actual).usingRecursiveComparison().ignoringFields("created").isEqualTo(expected);
    }

}