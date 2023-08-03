package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service("itemService")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepo;
    private UserService userService;
    private CommentRepository commentRepo;
    private ItemMapperService itemMapperService;

    @Override
    public ItemDto addNewItem(Long ownerId, ItemDto itemDto) {
        Item itemForSave = itemMapperService.addNewItem(ownerId, itemDto);
        Item item = itemRepo.save(itemForSave);
        return ItemMapper.makeDtoFromItem(item)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        validateId(itemId);
        validateId(userId);
        Item item = itemRepo.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Item is not found"));
        return itemMapperService.getItemDto(item, userId);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        List<Item> allItems = itemRepo.findAllByOwnerIdOrderById(userId);
        return itemMapperService.getItems(allItems);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDtoWithUpdate) {
        Item itemForUpdate = itemMapperService.prepareItemToUpdate(ownerId, itemId, itemDtoWithUpdate);
        Item itemUpdated = itemRepo.save(itemForUpdate);
        return ItemMapper.makeDtoFromItem(itemUpdated)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public List<ItemDto> searchForItems(String text) {
        List<ItemDto> searchResult = new ArrayList<>();
        if (!text.isBlank()) {
            searchResult = itemRepo.findByText(text).stream()
                    .map(item -> ItemMapper.makeDtoFromItem(item)
                            .orElseThrow(() -> new NullPointerException("dto объект не найден")))
                    .collect(Collectors.toList());
        }
        return searchResult;
    }

    @Override
    public CommentDto addNewCommentToItem(CommentRequestDto requestDto) {
        return CommentMapper
                .entityToDto(commentRepo.save(itemMapperService.prepareCommentToSave(requestDto)));
    }

    @Override
    public boolean deleteItem(Long ownerId, Long itemId) {
        User owner = UserMapper.makeUserWithId(userService.getUser(ownerId))
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
        itemRepo.delete(ItemMapper.makeItem(getItem(itemId, ownerId), owner)
                .orElseThrow(() -> new NullPointerException("объект не найден")));
        return true;
    }

    @Override
    public void clearAll() {
        itemRepo.deleteAll();
    }

    private void validateId(Long id) {
        if (id < 1) {
            log.warn("id {} incorrect", id);
            throw new IncorrectIdException("id can't be less then 1");
        }
    }
}
