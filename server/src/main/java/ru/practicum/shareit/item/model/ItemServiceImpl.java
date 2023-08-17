package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserService;

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
        return ItemMapper.makeDtoFromItem(item).get();
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        validateId(itemId);
        validateId(userId);
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
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
        return ItemMapper.makeDtoFromItem(itemUpdated).get();
    }

    @Override
    public List<ItemDto> searchForItems(String text) {
        List<ItemDto> searchResult = new ArrayList<>();
        if (!text.isBlank()) {
            searchResult = itemRepo.findByText(text).stream()
                    .map(item -> ItemMapper.makeDtoFromItem(item).get())
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
    public void deleteItem(Long ownerId, Long itemId) {
        User owner = UserMapper.makeUserWithId(userService.getUser(ownerId)).get();
        Item item = ItemMapper.makeItem(getItem(itemId, ownerId), owner).get();
        itemRepo.delete(item);
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
