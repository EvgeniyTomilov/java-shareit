package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class ItemRequestRepositoryTest {

    private final TestEntityManager entityManager;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void whenFindAllByRequesterId() {
        User user1 = User.builder()
                .email("email1@email.com")
                .name("name1")
                .build();
        User user2 = User.builder()
                .email("email2@email.com")
                .name("name2")
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("desc1")
                .requester(user1)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("desc2")
                .requester(user1)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest3 = ItemRequest.builder()
                .description("desc3")
                .requester(user2)
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);
        entityManager.persist(itemRequest3);
        List<ItemRequest> expected = List.of(itemRequest1, itemRequest2);

        List<ItemRequest> actual = itemRequestRepository.findAllByRequesterId(user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
