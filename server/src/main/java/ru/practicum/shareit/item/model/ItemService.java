package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getItems(Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId);

    void clearAll();

    Collection<ItemDto> searchForItems(String text);

    CommentDto addNewCommentToItem(CommentRequestDto requestDto);
}