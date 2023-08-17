package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;

import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.HeaderUserIdConst.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER_USER_ID) Long authorId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentRequestDto requestDto) {
        log.info("SERVER: Add new comment {} to item: {} - Started", requestDto, itemId);

        requestDto.setAuthorId(authorId);
        requestDto.setItemId(itemId);

        CommentDto commentDto = itemService.addNewCommentToItem(requestDto);
        log.info("SERVER: Comment added to item id: {} - Finished", itemId);
        return commentDto;
    }

    @PostMapping
    public ItemDto add(@RequestHeader(HEADER_USER_ID) Long ownerId,
                       @RequestBody ItemDto itemDto) {
        log.info("SERVER: add: {} - Started", itemDto);
        ItemDto itemDtoFromRepo = itemService.addNewItem(ownerId, itemDto);
        log.info("SERVER: create: {} - Finished", itemDtoFromRepo);
        return itemDtoFromRepo;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("SERVER: Update {} for item id: {} by user id {}  - Started", itemDto, itemId, userId);
        ItemDto itemDtoFromRepo = itemService.updateItem(userId, itemId, itemDto);
        log.info("SERVER: update: {} - Finished", itemDtoFromRepo);
        return itemDtoFromRepo;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER_USER_ID) Long ownerId) {
        log.info("SERVER: GetItems for user id {} - Started", ownerId);
        List<ItemDto> itemsOfUser = itemService.getItems(ownerId);
        log.info("SERVER: Found {} items of user id {} - GetItems Finished", itemsOfUser.size(), ownerId);
        return itemsOfUser;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        log.info("SERVER: Search for item id {} - Started", itemId);
        ItemDto itemDto = itemService.getItem(itemId, userId);
        log.info("SERVER: item {} was found", itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("SERVER: Search for available items with text '{}' - Started", text.toLowerCase());
        Collection<ItemDto> searchResult = itemService.searchForItems(text);
        log.info("SERVER: {} available items with text '{}' was found - Finished", searchResult.size(), text.toLowerCase());
        return searchResult;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        log.info("SERVER: Delete item id {} user id {} - Started", itemId, userId);
        itemService.deleteItem(userId, itemId);
        log.info("SERVER: item id {} was deleted", itemId);
    }

    @DeleteMapping
    public void clearAll() {
        log.info("SERVER: Total clear - Started");
        itemService.clearAll();
        log.info("SERVER: All items was deleted");
    }
}
