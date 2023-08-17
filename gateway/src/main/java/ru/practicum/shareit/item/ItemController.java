package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDtoGateway;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoGateway;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.utils.HeaderUserIdConst.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader(HEADER_USER_ID) Long authorId,
                                             @Positive @PathVariable Long itemId,
                                             @Valid @RequestBody CommentRequestDtoGateway requestDto) {
        log.info("GATEWAY: Add new comment {} to item: {} - Started", requestDto, itemId);

        ResponseEntity<Object> commentDto = itemClient.addNewCommentToItem(requestDto, authorId, itemId);
        log.info("GATEWAY: Comment added to item id: {} - Finished", itemId);
        return commentDto;
    }

    @PostMapping
    public ResponseEntity<Object> add(@Positive @RequestHeader(HEADER_USER_ID) Long ownerId,
                                      @Valid @RequestBody ItemDtoGateway itemDto) {
        log.info("GATEWAY: add: {} - Started", itemDto);
        ResponseEntity<Object> itemDtoFromRepo = itemClient.addNewItem(ownerId, itemDto);
        log.info("GATEWAY: create: {} - Finished", itemDtoFromRepo);
        return itemDtoFromRepo;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader(HEADER_USER_ID) Long userId,
                                         @Positive @PathVariable Long itemId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("GATEWAY: Update {} for item id: {} by user id {}  - Started", itemDto, itemId, userId);
        ResponseEntity<Object> itemDtoFromRepo = itemClient.updateItem(userId, itemId, itemDto);
        log.info("GATEWAY: update: {} - Finished", itemDtoFromRepo);
        return itemDtoFromRepo;
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@Positive @RequestHeader(HEADER_USER_ID) Long ownerId) {
        log.info("GATEWAY: GetItems for user id {} - Started", ownerId);
        ResponseEntity<Object> itemsOfUser = itemClient.getItems(ownerId);
        log.info("GATEWAY: Found items of user id {} - GetItems Finished", ownerId);
        return itemsOfUser;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @RequestHeader(HEADER_USER_ID) Long userId,
                                          @Positive @PathVariable Long itemId) {
        log.info("GATEWAY: Search for item id {} - Started", itemId);
        ResponseEntity<Object> itemDto = itemClient.getItem(itemId, userId);
        log.info("GATEWAY: item {} was found", itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(required = false) String text) {
        log.info("GATEWAY: Search for available items with text '{}' - Started", text.toLowerCase());
        ResponseEntity<Object> searchResult = itemClient.searchForItems(text);
        log.info("GATEWAY: found text '{}' in Item - Finished", text.toLowerCase());
        return searchResult;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                           @Positive @PathVariable Long itemId) {
        log.info("GATEWAY: Delete item id {} user id {} - Started", itemId, userId);
        itemClient.deleteItem(userId, itemId);
        log.info("GATEWAY: item id {} was deleted", itemId);
    }

}
