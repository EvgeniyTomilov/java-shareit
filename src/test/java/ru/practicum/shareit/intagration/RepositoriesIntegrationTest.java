package ru.practicum.shareit.intagration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Transactional
@DataJpaTest
public class RepositoriesIntegrationTest {

    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;

    @Autowired
    public RepositoriesIntegrationTest(UserService userService, BookingService bookingService, ItemService itemService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.itemService = itemService;
    }

    @Test
    public void getItemsTest() {
        try {
            userService.addUser(new UserDto(23L, "Ivan", "ivan@mail.ru"));
        } catch (Exception e) {
            fail("Can't add new User via UserService");
        }

        try {
            itemService.addNewItem(
                    23L,
                    new ItemDto(
                            1L,                             // id
                            "Sample Item",                  // name
                            "This is a sample item.",       // description
                            true,                           // available
                            123L,                           // ownerId
                            456L,                           // requestId
                            new ArrayList<>(),              // comments (empty list)
                            new BookingForItemDto(),     // lastBooking (instance of BookingForItemDto)
                            new BookingForItemDto()      // nextBooking (instance of BookingForItemDto)
                    ));
        } catch (Exception e) {
            fail("Can't add new Item via ItemService");
        }

        ItemDto item = itemService.getItem(1L, 23L);

        assertEquals(item.getId(), 1L);
        assertEquals(item.getOwnerId(), 23L);
    }


}
