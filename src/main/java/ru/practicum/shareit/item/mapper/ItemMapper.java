package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.exception.IncorrectItemDtoException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public final class ItemMapper {
    public static Item makeItem(ItemDto itemDto, User user) {
        Item item = new Item();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        item.setOwner(user);

        if (!StringUtils.isBlank(name)) {
            item.setName(itemDto.getName());
        } else {
            throw new IncorrectItemDtoException("Item's name is not found");
        }
        if (!StringUtils.isBlank(description)) {
            item.setDescription(itemDto.getDescription());
        } else {
            throw new IncorrectItemDtoException("Item's description is not found");
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        } else {
            throw new IncorrectItemDtoException("Available-status of item not found");
        }

        return item;
    }

    public static Item makeItemForUpdate(ItemDto oldItemDto, ItemDto itemDtoWithUpdate) {
        Item itemUpd = new Item();

        itemUpd.setAvailable(oldItemDto.getAvailable());
        itemUpd.setId(oldItemDto.getId());
        itemUpd.setOwner(oldItemDto.getOwner());
        itemUpd.setDescription(oldItemDto.getDescription());
        itemUpd.setName(oldItemDto.getName());

        if (itemDtoWithUpdate.getName() != null) {
            itemUpd.setName(itemDtoWithUpdate.getName());
        }

        if (itemDtoWithUpdate.getDescription() != null) {
            itemUpd.setDescription(itemDtoWithUpdate.getDescription());
        }

        if (itemDtoWithUpdate.getAvailable() != null) {
            itemUpd.setAvailable(itemDtoWithUpdate.getAvailable());
        }
        return itemUpd;
    }

    public static ItemDto makeDtoFromItem(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setName(item.getName());
        itemDto.setId(item.getId());
        itemDto.setOwner(item.getOwner());

        return itemDto;
    }
}
