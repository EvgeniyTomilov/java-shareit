package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class ItemRepositoryTest {

    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .name("name")
                .build();

        item1 = Item.builder()
                .name("firstName")
                .description("desc")
                .isAvailable(true)
                .owner(user)
                .build();

        item2 = Item.builder()
                .name("SecondName")
                .description("desc")
                .isAvailable(true)
                .owner(user)
                .build();
    }

    @Test
    void whenFindByText() {
        Item item3 = Item.builder()
                .name("full")
                .description("desc")
                .isAvailable(true)
                .owner(user)
                .build();
        entityManager.persist(user);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        List<Item> expected = List.of(item1, item2);

        List<Item> actual = itemRepository.findByText("name");

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByOwnerIdOrderById() {
        User user2 = User.builder()
                .email("email2@email.com")
                .name("name2")
                .build();
        Item item3 = Item.builder()
                .name("full")
                .description("desc")
                .isAvailable(true)
                .owner(user2)
                .build();
        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        List<Item> expected = List.of(item1, item2);

        List<Item> actual = itemRepository.findAllByOwnerIdOrderById(user.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByRequestId() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .requester(user)
                .description("desc1")
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .requester(user)
                .description("desc2")
                .created(LocalDateTime.now())
                .build();
        item1.setRequest(itemRequest1);
        item2.setRequest(itemRequest2);
        entityManager.persist(user);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> expected = List.of(item1);

        List<Item> actual = itemRepository.findAllByRequestId(itemRequest1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
