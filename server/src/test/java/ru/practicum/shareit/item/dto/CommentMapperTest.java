package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void entityToDto() {
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        Item item = new Item();
        User author = new User();
        author.setName("Author");
        author.setId(1L);
        author.setEmail("a@a.a");

        Comment comment = new Comment(1L, "comment", item, author, created);

        CommentDto commentDto = new CommentDto(1L, "comment", author.getName(), created);

        CommentDto expectedComment = new CommentDto();
        expectedComment = CommentMapper.entityToDto(comment);

        assertEquals(expectedComment, commentDto);

    }
}