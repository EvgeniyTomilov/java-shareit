package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(long userId, Item item);

    Item getItem(long itemId);

    Item updateItem(long userId, Item item);

    void deleteItem(long userId, long itemId);

    List<ItemDto> getItemsOfUser(long userId);

    List<ItemDto> getAllItems();

    void clearAll();
}
