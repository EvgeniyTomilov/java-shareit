package ru.practicum.shariet.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shariet.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shariet.util.Utils.SHARER_USER_ID;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> addRequest(@Positive @RequestHeader(SHARER_USER_ID) Long requesterId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("GATEWAY: Add new request: {} by user id {} - Started", itemRequestDto, requesterId);
        ResponseEntity<Object> itemRequestDtoFromRepo = itemRequestClient.addNewItemRequest(requesterId, itemRequestDto);
        log.info("GATEWAY: Add new request: {} - Finished", itemRequestDtoFromRepo);
        return itemRequestDtoFromRepo;
    }

    @GetMapping
    ResponseEntity<Object> getItemRequests(@Positive @RequestHeader(SHARER_USER_ID) Long requesterId) {
        log.info("GATEWAY: Get requests for user id: {} - Started", requesterId);
        return itemRequestClient.getItemRequests(requesterId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllItemRequests(@RequestHeader(SHARER_USER_ID) Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "20") Integer size) {

        log.info("GATEWAY: Get All requests - Started");
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                          @PathVariable Long requestId) {
        log.info("GATEWAY: Get request id: {} - Started", requestId);
        ResponseEntity<Object> requestItemDto = itemRequestClient.getItemRequest(userId, requestId);
        log.info("GATEWAY: Request id {} was found - Finished", requestId);

        return requestItemDto;
    }


}