package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


public final class ItemMapper {

    private ItemMapper() {
    }

    public static Optional<Item> makeItem(ItemDto itemDto, User owner) {

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setId(itemDto.getId());

        return Optional.of(item);
    }

    public static Optional<Item> makeItemWithRequest(ItemDto itemDto, User owner, ItemRequest request) {

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setId(itemDto.getId());
        item.setRequest(request);

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
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

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

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

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

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return Optional.of(itemDto);
    }
}
