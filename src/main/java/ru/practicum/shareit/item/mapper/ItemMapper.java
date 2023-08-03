package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public final class ItemMapper {

    public static Optional<Item> makeItem(ItemDto itemDto, User owner) {

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setId(itemDto.getId());
        return Optional.of(item);
    }

    public static Optional<Item> makeItemForUpdate(ItemDto oldItemDto, ItemDto itemDtoWithUpdate, User owner) {
        Item itemUpd = new Item();

        itemUpd.setIsAvailable(oldItemDto.getAvailable());
        itemUpd.setId(oldItemDto.getId());
        itemUpd.setOwner(owner);
        itemUpd.setDescription(oldItemDto.getDescription());
        itemUpd.setName(oldItemDto.getName());

        if (itemDtoWithUpdate.getName() != null) {
            itemUpd.setName(itemDtoWithUpdate.getName());
        }

        if (itemDtoWithUpdate.getDescription() != null) {
            itemUpd.setDescription(itemDtoWithUpdate.getDescription());
        }

        if (itemDtoWithUpdate.getAvailable() != null) {
            itemUpd.setIsAvailable(itemDtoWithUpdate.getAvailable());
        }
        return Optional.of(itemUpd);
    }

    public static Optional<ItemDto> makeDtoFromItem(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(item.getIsAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setName(item.getName());
        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());

        return Optional.of(itemDto);
    }

    public static Optional<ItemDto> makeDtoFromItemWithBooking(Item item, List<CommentDto> commentsForItemDto, BookingForItemDto lastBooking,
                                                               BookingForItemDto nextBooking) {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(item.getIsAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setName(item.getName());
        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());
        itemDto.setComments(commentsForItemDto);

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);

        return Optional.of(itemDto);
    }

    public static Optional<ItemDto> makeDtoFromItemWithComment(Item item, List<CommentDto> commentsForItemDto) {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(item.getIsAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setName(item.getName());
        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());
        itemDto.setComments(commentsForItemDto);

        return Optional.of(itemDto);
    }
}
