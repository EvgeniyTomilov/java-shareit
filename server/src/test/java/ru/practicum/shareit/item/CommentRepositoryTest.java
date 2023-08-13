package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class CommentRepositoryTest {

    private final TestEntityManager entityManager;
    private final CommentRepository commentRepository;

    @Test
    void whenFindAllByItemIdOrderById() {
        User user = User.builder()
                .email("email@email.com")
                .name("name")
                .build();
        Item item = Item.builder()
                .name("name")
                .description("desc")
                .isAvailable(true)
                .owner(user)
                .build();
        Comment comment1 = Comment.builder()
                .text("text1")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .text("text2")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment1);
        entityManager.persist(comment2);
        List<Comment> expected = List.of(comment1, comment2);

        List<Comment> actual = commentRepository.findAllByItemIdOrderById(item.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
