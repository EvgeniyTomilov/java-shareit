package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestMapperService {
    ItemRequest prepareForSaveItemRequest(Long requesterId, ItemRequestDto dto);

    boolean requesterValidate(Long requesterId);

    ItemRequestDto prepareForReturnDto(ItemRequest itemRequest);

    List<ItemRequestDto> prepareForReturnListDto(List<ItemRequest> itemRequests);
}
