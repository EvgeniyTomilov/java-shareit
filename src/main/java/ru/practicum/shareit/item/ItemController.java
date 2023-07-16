package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.util.Utils.SHARER_USER_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader(SHARER_USER_ID) long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("add: {} - Started", itemDto);
        ItemDto itemDtoFromRepo = itemService.addItem(userId, itemDto);
        log.info("create: {} - Finished", itemDtoFromRepo);
        return itemDtoFromRepo;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(SHARER_USER_ID) long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Update {} for item id: {} by user id {}  - Started", itemDto, itemId, userId);
        ItemDto itemDtoFromRepo = itemService.updateItem(userId, itemId, itemDto);
        log.info("update: {} - Finished", itemDtoFromRepo);
        return itemDtoFromRepo;
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GetItems for user id {} - Started", userId);
        Collection<ItemDto> itemsOfUser = itemService.getItems(userId);
        log.info("Found {} items of user id {} - GetItems Finished", itemsOfUser.size(), userId);
        return itemsOfUser;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(SHARER_USER_ID) long userId,
                           @PathVariable long itemId) {
        log.info("Search for item id {} - Started", itemId);
        ItemDto itemDto = itemService.getItem(itemId);
        log.info("item {} was found", itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("Search for available items with text '{}' - Started", text.toLowerCase());
        Collection<ItemDto> searchResult = itemService.searchAvailableItems(text);
        log.info("{} available items with text '{}' was found - Finished", searchResult.size(), text.toLowerCase());
        return searchResult;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID) long userId,
                           @PathVariable long itemId) {
        log.info("Delete item id {} user id {} - Started", itemId, userId);
        boolean isDel = itemService.deleteItem(userId, itemId);
        log.info("item id {} was deleted - {} ", itemId, isDel);
    }

    @DeleteMapping
    public void clearAll() {
        log.info("Total clear - Started");
        itemService.clearAll();
        log.info("All items was deleted");
    }
}