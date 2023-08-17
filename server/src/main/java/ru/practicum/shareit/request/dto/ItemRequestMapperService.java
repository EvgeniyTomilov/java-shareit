package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class ItemRequestMapperService {
    private UserService userService;
    private ItemRepository itemRepo;


    public ItemRequest prepareForSaveItemRequest(Long requesterId, ItemRequestDto dto) {
        User requester = UserMapper.makeUserWithId(userService.getUser(requesterId)).get();
        return ItemRequestMapper.makeItemRequest(dto, requester).get();
    }

    public boolean requesterValidate(Long requesterId) {
        validateId(requesterId);
        userService.getUser(requesterId);
        return true;
    }

    private void validateId(Long id) {
        if (id < 1) {
            log.warn("id {} incorrect", id);
            throw new IncorrectIdException("id can't be less then 1");
        }
    }

    public ItemRequestDto prepareForReturnDto(ItemRequest itemRequest) {
        List<Item> itemsForRequest = itemRepo.findAllByRequestId(itemRequest.getId());
        List<ItemDto> itemsDtoForRequest = itemsForRequest.stream()
                .map(item -> ItemMapper.makeDtoFromItem(item).get())
                .collect(Collectors.toList());

        log.info("Размер списка вещей, которые предложены в ответ на запрос {}, составляет: {}",
                itemRequest.getId(), itemsForRequest.size());

        ItemRequestDto itemRequestDto;
        if (itemsForRequest.size() == 0) {
            itemRequestDto = ItemRequestMapper.makeItemRequestDto(itemRequest).get();
        } else {
            itemRequestDto = ItemRequestMapper.makeItemRequestDtoWithItemsList(itemRequest, itemsDtoForRequest).get();
        }
        return itemRequestDto;
    }

    public List<ItemRequestDto> prepareForReturnListDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(itemRequest -> prepareForReturnDto(itemRequest))
                .collect(Collectors.toList());
    }
}
