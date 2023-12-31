package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public final class CommentMapper {

    public Comment requestToEntity(Item item, User author, String text) {
        Comment newComment = new Comment();
        newComment.setItem(item);
        newComment.setAuthor(author);
        newComment.setText(text);
        newComment.setCreated(LocalDateTime.now());
        return newComment;
    }

    public CommentDto entityToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}