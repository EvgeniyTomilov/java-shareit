package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepo {

    Item save(long userId, Item item);

    Item getItem(long itemId);

    Item update(long userId, Item item);

    void delete(long userId, long itemId);

    List<ItemDto> getItemsOfUser(long userId);

    List<ItemDto> getAllItems();

    void clearAll();
}
