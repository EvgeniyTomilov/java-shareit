package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service("itemService")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(
            ItemRepository itemRepository,
            UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        Item item = itemRepository.addItem(userId, ItemMapper.makeItem(itemDto,
                UserMapper.makeUserWithId(userService.getUser(userId))));
        return ItemMapper.makeDtoFromItem(item);
    }

    @Override
    public ItemDto getItem(long itemId) {
        validateId(itemId);
        Item item = itemRepository.getItem(itemId);
        return ItemMapper.makeDtoFromItem(item);
    }

    private void validateId(long id) {
        if (id < 1) {
            log.warn("id {} incorrect", id);
            throw new IncorrectIdException("id can't be less then 1");
        }
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userService.getUser(userId);
        return itemRepository.getItemsOfUser(userId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDtoWithUpdate) {
        validateId(itemId);
        ItemDto itemFromRepo = getItem(itemId);
        if (itemFromRepo.getOwner().getId() != null && itemFromRepo.getOwner().getId() == userId) {
            itemDtoWithUpdate.setId(itemId);
            Item itemUpdated = itemRepository.updateItem(userId, ItemMapper.makeItemForUpdate(itemFromRepo, itemDtoWithUpdate));
            return ItemMapper.makeDtoFromItem(itemUpdated);
        } else {
            log.error("User Id {} has not item", userId);
            throw new ItemNotFoundException("Item is not found");
        }
    }

    @Override
    public boolean deleteItem(long userId, long itemId) {
        userService.getUser(userId);
        itemRepository.deleteItem(userId, itemId);
        return true;
    }

    @Override
    public void clearAll() {
        itemRepository.clearAll();
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        List<ItemDto> searchResult = new ArrayList<>();
        if (!text.isBlank()) {
            searchResult = itemRepository.getAllItems().stream()
                    .filter(itemDto -> itemDto.getAvailable().equals(true))
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return searchResult;
    }
}
