package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceImplIntegrationTest {
    private User owner;
    private ItemDto itemDto;

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("OwnerOfItem")
                .email("o@o.o")
                .build();

        itemDto = ItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
    }

    @Test
    void whenAddNewItem() {
        userRepository.save(owner);
        ItemDto expected = itemService.addNewItem(owner.getId(), itemDto);

        ItemDto actual = itemService.getItem(expected.getId(), owner.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("comments").isEqualTo(expected);
    }

    @Test
    void whenGetItems() {
        userRepository.save(owner);
        ItemDto dto = ItemDto.builder()
                .name("item test2")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
        ItemDto itemDto1 = itemService.addNewItem(owner.getId(), itemDto);
        ItemDto itemDto2 = itemService.addNewItem(owner.getId(), dto);
        List<ItemDto> expected = List.of(itemDto1, itemDto2);

        List<ItemDto> actual = itemService.getItems(owner.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("comments").isEqualTo(expected);
    }

    @Test
    void whenUpdateItem() {
        userRepository.save(owner);
        ItemDto itemDtoForUpdate = ItemDto.builder()
                .name("new name")
                .description("new description")
                .ownerId(owner.getId())
                .available(Boolean.TRUE)
                .build();
        ItemDto oldItem = itemService.addNewItem(owner.getId(), itemDto);

        ItemDto actual = itemService.updateItem(oldItem.getId(), owner.getId(), itemDtoForUpdate);
        itemDtoForUpdate.setId(oldItem.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("comments").isEqualTo(itemDtoForUpdate);
    }

    @Test
    void whenSearchForItems() {
        userRepository.save(owner);
        itemDto.setName("name");
        itemDto.setDescription("description");
        ItemDto dto = ItemDto.builder()
                .name("namer")
                .description("descriptions")
                .available(Boolean.TRUE)
                .build();
        ItemDto itemDto1 = itemService.addNewItem(owner.getId(), itemDto);
        ItemDto itemDto2 = itemService.addNewItem(owner.getId(), dto);
        List<ItemDto> expected = List.of(itemDto1, itemDto2);

        Collection<ItemDto> actual = itemService.searchForItems("name");

        assertThat(actual).usingRecursiveComparison().ignoringFields("comments").isEqualTo(expected);
    }

    @Test
    void whenAddNewCommentToItem() {
        userRepository.save(owner);
        ItemDto item = itemService.addNewItem(owner.getId(), itemDto);
        CommentRequestDto comment = CommentRequestDto.builder()
                .text("text")
                .authorId(owner.getId())
                .itemId(item.getId())
                .build();
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now())
                .booker(owner)
                .item(ItemMapper.makeItem(item, owner).get())
                .status(StatusOfBooking.APPROVED)
                .build();
        bookingRepository.save(booking);

        CommentDto expected = itemService.addNewCommentToItem(comment);

        CommentDto actual = itemService.getItem(item.getId(), owner.getId()).getComments().get(0);

        assertThat(actual).usingRecursiveComparison().ignoringFields("created").isEqualTo(expected);
    }

    @Test
    void whenDeleteItem() {
        userRepository.save(owner);
        ItemDto item = itemService.addNewItem(owner.getId(), itemDto);

        itemService.deleteItem(owner.getId(), item.getId());
        Optional<Item> actual = itemRepository.findById(item.getId());

        assertThat(actual).isEmpty();
    }

    @Test
    void whenClearAll() {
        userRepository.save(owner);
        ItemDto dto = ItemDto.builder()
                .name("namer")
                .description("descriptions")
                .available(Boolean.TRUE)
                .build();
        itemService.addNewItem(owner.getId(), itemDto);
        itemService.addNewItem(owner.getId(), dto);

        itemService.clearAll();
        List<Item> actual = itemRepository.findAll();

        assertThat(actual).isEmpty();
    }

}