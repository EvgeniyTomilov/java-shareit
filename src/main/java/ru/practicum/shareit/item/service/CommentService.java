package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentService {
    Comment save(Comment prepareCommentToSave);

    List<Comment> findAllByItemIdOrderById(Long id);
}
