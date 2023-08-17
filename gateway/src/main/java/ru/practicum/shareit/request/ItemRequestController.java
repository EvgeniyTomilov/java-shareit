package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.utils.HeaderUserIdConst.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> addRequest(@Positive @RequestHeader(HEADER_USER_ID) Long requesterId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("GATEWAY: Add new request: {} by user id {} - Started", itemRequestDto, requesterId);
        ResponseEntity<Object> itemRequestDtoFromRepo = itemRequestClient.addNewItemRequest(requesterId, itemRequestDto);
        log.info("GATEWAY: Add new request: {} - Finished", itemRequestDtoFromRepo);
        return itemRequestDtoFromRepo;
    }

    @GetMapping
    ResponseEntity<Object> getItemRequests(@Positive @RequestHeader(HEADER_USER_ID) Long requesterId) {
        log.info("GATEWAY: Get requests for user id: {} - Started", requesterId);
        return itemRequestClient.getItemRequests(requesterId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllItemRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                              @Valid @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                              @Valid @RequestParam(required = false, defaultValue = "20") @Min(1) Integer size) {

        log.info("GATEWAY: Get All requests - Started");
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getItemRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                          @PathVariable Long requestId) {
        log.info("GATEWAY: Get request id: {} - Started", requestId);
        ResponseEntity<Object> requestItemDto = itemRequestClient.getItemRequest(userId, requestId);
        log.info("GATEWAY: Request id {} was found - Finished", requestId);

        return requestItemDto;
    }


}
