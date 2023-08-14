package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static Optional<ItemRequest> makeItemRequest(ItemRequestDto dto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        if (dto.getId() != null) {
            itemRequest.setId(itemRequest.getId());
        }
        itemRequest.setRequester(requester);
        itemRequest.setDescription(dto.getDescription());
        return Optional.of(itemRequest);
    }

    public static Optional<ItemRequestDto> makeItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        if (itemRequest.getId() != null) {
            itemRequestDto.setId(itemRequest.getId());
        }
        if (itemRequest.getRequester() != null) {
            itemRequestDto.setRequesterId(itemRequest.getRequester().getId());
        }
        if (itemRequest.getDescription() != null) {
            itemRequestDto.setDescription(itemRequest.getDescription());
        }
        if (itemRequest.getCreated() != null) {
            itemRequestDto.setCreated(itemRequest.getCreated());
        }
        return Optional.of(itemRequestDto);
    }

    public static Optional<ItemRequestDto> makeItemRequestDtoWithItemsList(ItemRequest itemRequest, List<ItemDto> itemsDtoForRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        if (itemRequest.getId() != null) {
            itemRequestDto.setId(itemRequest.getId());
        }
        if (itemRequest.getRequester() != null) {
            itemRequestDto.setRequesterId(itemRequest.getRequester().getId());
        }
        if (itemRequest.getDescription() != null) {
            itemRequestDto.setDescription(itemRequest.getDescription());
        }
        if (itemRequest.getCreated() != null) {
            itemRequestDto.setCreated(itemRequest.getCreated());
        }
        if (itemsDtoForRequest != null) {
            itemRequestDto.setItems(itemsDtoForRequest);
        }
        return Optional.of(itemRequestDto);
    }
}