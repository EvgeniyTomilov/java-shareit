package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Utils.SHARER_USER_ID;

@Validated
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto addRequest(@RequestHeader(SHARER_USER_ID) Long requesterId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Add new request: {} by user id {} - Started", itemRequestDto, requesterId);
        ItemRequestDto itemRequestDtoFromRepo = itemRequestService.addNewItemRequest(requesterId, itemRequestDto);
        log.info("Add new request: {} - Finished", itemRequestDtoFromRepo);
        return itemRequestDtoFromRepo;
    }

    @GetMapping
    List<ItemRequestDto> getItemRequests(@RequestHeader(SHARER_USER_ID) Long requesterId) {
        log.info("Get requests for user id: {} - Started", requesterId);
        List<ItemRequestDto> listOfRequestsDto = itemRequestService.getItemRequests(requesterId);
        log.info("Size of founded List of requests is {} - Finished", listOfRequestsDto.size());
        return listOfRequestsDto;
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllItemRequests(@RequestHeader(SHARER_USER_ID) Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "20") @Positive Integer size) {

        log.info("Get All requests - Started");
        List<ItemRequestDto> listOfRequestsDto = itemRequestService.getAllItemRequests(userId, from, size);
        log.info("Size of founded List of requests is {} - Finished", listOfRequestsDto.size());
        return listOfRequestsDto;
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                  @PathVariable Long requestId) {
        log.info("Get request id: {} - Started", requestId);
        ItemRequestDto requestItemDto = itemRequestService.getItemRequest(userId, requestId);
        log.info("Request id {} was found - Finished", requestId);

        return requestItemDto;
    }
}