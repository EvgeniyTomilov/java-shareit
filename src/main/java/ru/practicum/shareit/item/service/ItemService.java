package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto getItem(long itemId);

    Collection<ItemDto> getItems(long userId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    boolean deleteItem(long userId, long itemId);

    void clearAll();

    Collection<ItemDto> searchAvailableItems(String text);
}
