package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentService {
    Comment save(Comment prepareCommentToSave);

    List<Comment> findAllByItemIdOrderById(Long id);
}
