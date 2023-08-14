package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemControllerTest {
    ItemController controller;
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    UserDto userDto = new UserDto();
    User firstUser = new User();

    @BeforeEach
    public void beforeEach() {
        controller = new ItemController(itemService);
        userDto.setName("First");
        userDto.setEmail("a1@a.mail.ru");
        firstUser = UserMapper.makeUserWithId(userService.create(userDto))
                .orElseThrow(() -> new NullPointerException("User объект не создан"));
    }

    @AfterEach
    public void afterEach() {
        controller.clearAll();
        userService.clearAll();
    }

    @Test
    void shouldAddItemAndGetIt() {
        ItemDto firstItemDto = new ItemDto();
        firstItemDto.setName("1stItem");
        firstItemDto.setDescription("1stDescription");
        firstItemDto.setAvailable(false);
        ItemDto firstItemDtoFromRepo = controller.add(firstUser.getId(), firstItemDto);
        firstItemDtoFromRepo.setComments(new ArrayList<CommentDto>());

        assertEquals(firstItemDtoFromRepo, controller.getItem(firstUser.getId(), firstItemDtoFromRepo.getId()),
                "Item не создано или не получено из хранилища");
    }

    @Test
    void shouldMakeUpdate() {
        ItemDto firstItemDto = new ItemDto();
        firstItemDto.setName("1stItem");
        firstItemDto.setDescription("1stDescription");
        firstItemDto.setAvailable(false);
        ItemDto firstItemDtoFromRepo = controller.add(firstUser.getId(), firstItemDto);

        ItemDto firstUpdateItemDto = new ItemDto();
        firstUpdateItemDto.setName("1stItemUpdate");
        firstUpdateItemDto.setDescription("1stDescriptionUpdate");
        firstUpdateItemDto.setAvailable(true);
        ItemDto firstUpdateItemDtoFromRepo = controller.update(firstUser.getId(), firstUser.getId(), firstUpdateItemDto);
        firstUpdateItemDtoFromRepo.setComments(new ArrayList<CommentDto>());
        assertEquals(firstUpdateItemDtoFromRepo, controller.getItem(1L, 1L),
                "Item не обновлено или не получено из хранилища");
    }
}