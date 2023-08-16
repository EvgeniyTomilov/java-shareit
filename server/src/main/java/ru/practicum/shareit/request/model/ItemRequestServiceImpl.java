package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperService;
import ru.practicum.shareit.request.repo.ItemRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestMapperService itemRequestMapperService;
    private ItemRequestRepository itemRequestRepo;

    @Override
    public ItemRequestDto addNewItemRequest(Long requesterId, ItemRequestDto dto) {
        ItemRequest itemRequestForSave = itemRequestMapperService.prepareForSaveItemRequest(requesterId, dto);
        ItemRequest itemRequest = itemRequestRepo.save(itemRequestForSave);

        return itemRequestMapperService.prepareForReturnDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Long requesterId) {
        itemRequestMapperService.requesterValidate(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepo.findAllByRequesterId(requesterId);
        List<ItemRequestDto> itemRequestsDto = itemRequestMapperService.prepareForReturnListDto(itemRequests);
        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created"));
        List<ItemRequest> answerList = itemRequestRepo.findAll(pageRequest)
                .stream()
                .collect(Collectors.toList());
        List<ItemRequestDto> answerDtoList = answerList.stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .map(itemRequest -> itemRequestMapperService.prepareForReturnDto(itemRequest))
                .collect(Collectors.toList());

        return answerDtoList;
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        itemRequestMapperService.requesterValidate(userId);
        ItemRequest itemRequest = itemRequestRepo.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Item is not found!"));
        return itemRequestMapperService.prepareForReturnDto(itemRequest);
    }
}
