package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shariet.item.dto.CommentDto;
import ru.practicum.shariet.item.dto.ItemDto;

import java.util.List;

public interface ItemMapperService {
    Item addNewItem(Long ownerId, ItemDto itemDto);

    ItemDto getItemDto(Item item, Long userId);

    List<ItemDto> getItems(List<Item> allItems);

    ItemDto getItemDtoForUser(Item item, List<CommentDto> commentsForItemDto);

    ItemDto getItemDtoForOwner(Item item, List<CommentDto> commentsForItemDto);

    Item prepareItemToUpdate(Long ownerId, Long itemId, ItemDto itemDtoWithUpdate);

    Comment prepareCommentToSave(CommentRequestDto requestDto);
}

